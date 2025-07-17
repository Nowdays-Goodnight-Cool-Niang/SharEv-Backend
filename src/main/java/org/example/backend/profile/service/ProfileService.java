package org.example.backend.profile.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.account.repository.AccountRepository;
import org.example.backend.event.entity.Event;
import org.example.backend.event.repository.EventRepository;
import org.example.backend.profile.dto.response.ProfileDto;
import org.example.backend.profile.dto.response.ResponseProfileDto;
import org.example.backend.profile.dto.response.ResponseProfileInfoDto;
import org.example.backend.profile.entity.Profile;
import org.example.backend.profile.repository.ProfileRepository;
import org.example.backend.relation.entity.Relation.RelationId;
import org.example.backend.relation.repository.RelationRepository;
import org.example.backend.util.RandomNumberCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {
    private static final int START_RANDOM_NUMBER = 1;
    private static final int END_RANDOM_NUMBER = 12;

    private final ProfileRepository profileRepository;
    private final AccountRepository accountRepository;
    private final RelationRepository relationRepository;
    private final EventRepository eventRepository;
    private final RandomNumberCalculator randomNumberCalculator;

    public void join(UUID eventId, Long accountId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow();
        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        // TODO: pinNumber는 redis 통해 계산할 것(검증 -> redisson으로 분산락, 이중 체킹)

        profileRepository.save(new Profile(event, account, randomNumberCalculator.getRandom(1000, 9999),
                randomNumberCalculator.getRandom(START_RANDOM_NUMBER, END_RANDOM_NUMBER)));
    }

    @Transactional
    public ResponseProfileInfoDto updateInfo(UUID eventId, Long accountId, String introduce,
                                             String reminderExperience,
                                             String wantAgainExperience) {
        Profile profile = profileRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow();

        profile.updateInfo(introduce, reminderExperience, wantAgainExperience);

        return new ResponseProfileInfoDto(profile.getIntroduce(), profile.getReminderExperience(),
                profile.getWantAgainExperience());
    }

    public ResponseProfileDto getParticipantProfile(UUID eventId, Long accountId, Integer pinNumber) {
        Profile profile = profileRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow();

        Profile targetProfile = profileRepository.findByEventIdAndPinNumber(eventId, pinNumber)
                .orElseThrow();

        boolean registerFlag = relationRepository.existsById(
                new RelationId(profile.getId(), targetProfile.getId()));

        return new ProfileDto(targetProfile, registerFlag);
    }

    public ResponseProfileDto getMyParticipantProfile(UUID eventId, Long accountId) {
        Profile profile = profileRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow();

        return new ProfileDto(profile, false);
    }
}
