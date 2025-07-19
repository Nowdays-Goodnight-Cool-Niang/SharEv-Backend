package org.example.backend.profile.service;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.account.repository.AccountRepository;
import org.example.backend.event.entity.Event;
import org.example.backend.event.repository.EventRepository;
import org.example.backend.profile.dto.response.ProfileDto;
import org.example.backend.profile.dto.response.ResponsePinNumberOnlyDto;
import org.example.backend.profile.dto.response.ResponseProfileDto;
import org.example.backend.profile.dto.response.ResponseProfileInfoDto;
import org.example.backend.profile.entity.Profile;
import org.example.backend.profile.repository.ProfileRepository;
import org.example.backend.relation.entity.Relation.RelationId;
import org.example.backend.relation.repository.RelationRepository;
import org.example.backend.util.LockProcessor;
import org.example.backend.util.RandomNumberCalculator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {
    private static final int START_PIN_RANGE = 1_000;
    private static final int END_PIN_RANGE = 9_999;
    private static final int START_RANDOM_NUMBER = 1;
    private static final int END_RANDOM_NUMBER = 12;
    private static final String EVENT_PIN_PREFIX = "event";
    private static final String EVENT_PIN_SUFFIX = "pin-numbers";
    private static final Long EXPIRE_PIN_NUMBER_DAY = 3L;

    private final ProfileRepository profileRepository;
    private final AccountRepository accountRepository;
    private final RelationRepository relationRepository;
    private final EventRepository eventRepository;
    private final RandomNumberCalculator randomNumberCalculator;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LockProcessor lockProcessor;

    @Transactional
    public void join(UUID eventId, Long accountId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow();
        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        int pinNumber = getPinNumber(eventId);

        profileRepository.save(new Profile(event, account, pinNumber,
                randomNumberCalculator.getRandom(START_RANDOM_NUMBER, END_RANDOM_NUMBER)));
    }

    private static String calculateEventPinKey(UUID eventId) {
        return String.join(":", EVENT_PIN_PREFIX, eventId.toString(), EVENT_PIN_SUFFIX);
    }

    private int getPinNumber(UUID eventId) {
        Integer pinNumber = getUniquePinNumber(eventId);
        if (Objects.isNull(pinNumber)) {
            throw new RuntimeException("pin number 발급 도중 예외가 발생했습니다. 운영진에게 알려 주십시오.");
        }

        return pinNumber;
    }

    private Integer getUniquePinNumber(UUID eventId) {
        String eventPinKey = calculateEventPinKey(eventId);

        Integer pinNumber = (Integer) redisTemplate.opsForSet().pop(eventPinKey);

        if (Objects.nonNull(pinNumber)) {
            return pinNumber;
        }

        lockProcessor.lock(eventPinKey, key -> insertPinNumbers(eventId, key));

        return (Integer) redisTemplate.opsForSet().pop(eventPinKey);
    }

    private void insertPinNumbers(UUID eventId, String eventPinKey) {
        Boolean keyExistFlag = redisTemplate.hasKey(eventPinKey);

        if (Objects.isNull(keyExistFlag)) {
            throw new RuntimeException("pin number 확인 도중 예외가 발생했습니다. 운영진에게 알려 주십시오.");
        }

        if (keyExistFlag.equals(Boolean.TRUE)) {
            return;
        }

        Set<Integer> usingPinNumbers = profileRepository.findAllByEventId(eventId).stream()
                .map(ResponsePinNumberOnlyDto::getPinNumber)
                .collect(Collectors.toSet());

        Set<Integer> availablePinNumbers = IntStream.rangeClosed(START_PIN_RANGE, END_PIN_RANGE)
                .boxed()
                .filter(pinNumber -> !usingPinNumbers.contains(pinNumber))
                .collect(Collectors.toSet());

        redisTemplate.opsForSet().add(eventPinKey, availablePinNumbers);
        redisTemplate.expire(eventPinKey, Duration.ofDays(EXPIRE_PIN_NUMBER_DAY));
    }

    @Transactional
    public ResponseProfileInfoDto updateInfo(UUID eventId, Long accountId, String introduce,
                                             String proudestExperience,
                                             String toughExperience) {
        Profile profile = profileRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow();

        profile.updateInfo(introduce, proudestExperience, toughExperience);

        return new ResponseProfileInfoDto(profile.getIntroduce(), profile.getProudestExperience(),
                profile.getToughExperience());
    }

    public ResponseProfileDto getProfileByPinNumber(UUID eventId, Long accountId, Integer pinNumber) {
        Profile profile = profileRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow();

        Profile targetProfile = profileRepository.findByEventIdAndPinNumber(eventId, pinNumber)
                .orElseThrow();

        boolean registerFlag = relationRepository.existsById(
                new RelationId(profile.getId(), targetProfile.getId()));

        return new ProfileDto(targetProfile, registerFlag);
    }

    public ResponseProfileDto getMyProfile(UUID eventId, Long accountId) {
        Profile profile = profileRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow();

        return new ProfileDto(profile, false);
    }
}
