package sharev.card.service;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import sharev.account.entity.Account;
import sharev.account.exception.AccountNotFoundException;
import sharev.account.repository.AccountRepository;
import sharev.card.dto.response.ResponseCardDto;
import sharev.card.dto.response.ResponseCardInfoDto;
import sharev.card.dto.response.ResponseParticipantFlagDto;
import sharev.card.dto.response.ResponsePinNumberOnlyDto;
import sharev.card.entity.Card;
import sharev.card.exception.CardNotFoundException;
import sharev.card.exception.CardUncompletedException;
import sharev.card.exception.JoinAlreadyException;
import sharev.card.exception.KeyBlankException;
import sharev.card.exception.PinNumberGenerateException;
import sharev.card.repository.CardRepository;
import sharev.card_connection.entity.CardConnection.CardConnectionId;
import sharev.card_connection.repository.CardConnectionRepository;
import sharev.event.entity.Event;
import sharev.event.exception.EventNotFoundException;
import sharev.event.repository.EventRepository;
import sharev.event.util.EventKeyGenerator;
import sharev.util.LockProcessor;
import sharev.util.RandomNumberCalculator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {
    private static final int START_PIN_RANGE = 1_000;
    private static final int END_PIN_RANGE = 9_999;
    private static final int START_RANDOM_NUMBER = 1;
    private static final int END_RANDOM_NUMBER = 12;
    private static final Long EXPIRE_PIN_NUMBER_DAY = 3L;

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CardConnectionRepository cardConnectionRepository;
    private final EventRepository eventRepository;
    private final RandomNumberCalculator randomNumberCalculator;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LockProcessor lockProcessor;

    @Transactional
    public void join(UUID eventId, Long accountId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        int pinNumber = getPinNumber(eventId);

        try {
            cardRepository.save(new Card(event, account, pinNumber,
                    randomNumberCalculator.getRandom(START_RANDOM_NUMBER, END_RANDOM_NUMBER)));
        } catch (DataIntegrityViolationException e) {
            String eventPinKey = EventKeyGenerator.calculateEventPinKey(eventId);
            redisTemplate.opsForSet().add(eventPinKey, pinNumber);
            throw new JoinAlreadyException();
        }
    }

    private int getPinNumber(UUID eventId) {
        Integer pinNumber = getUniquePinNumber(eventId);
        if (Objects.isNull(pinNumber)) {
            throw new PinNumberGenerateException();
        }

        return pinNumber;
    }

    private Integer getUniquePinNumber(UUID eventId) {
        String eventPinKey = EventKeyGenerator.calculateEventPinKey(eventId);

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
            throw new KeyBlankException();
        }

        if (keyExistFlag.equals(Boolean.TRUE)) {
            return;
        }

        Set<Integer> usingPinNumbers = cardRepository.findAllByEventId(eventId).stream()
                .map(ResponsePinNumberOnlyDto::getPinNumber)
                .collect(Collectors.toSet());

        Integer[] availablePinNumbers = IntStream.rangeClosed(START_PIN_RANGE, END_PIN_RANGE)
                .filter(pinNumber -> !usingPinNumbers.contains(pinNumber))
                .boxed()
                .toArray(Integer[]::new);

        redisTemplate.opsForSet().add(eventPinKey, availablePinNumbers);
        redisTemplate.expire(eventPinKey, Duration.ofDays(EXPIRE_PIN_NUMBER_DAY));
    }

    @Transactional
    public ResponseCardInfoDto updateInfo(UUID eventId, Long accountId, String introduce,
                                          String proudestExperience,
                                          String toughExperience) {
        Card card = cardRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow(CardNotFoundException::new);

        card.updateInfo(introduce, proudestExperience, toughExperience);

        return new ResponseCardInfoDto(card.getIntroduce(), card.getProudestExperience(),
                card.getToughExperience());
    }

    public ResponseCardDto getCardByPinNumber(UUID eventId, Long accountId, Integer pinNumber) {
        Card card = cardRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow(CardNotFoundException::new);

        Card targetCard = cardRepository.findByEventIdAndPinNumber(eventId, pinNumber)
                .orElseThrow(CardNotFoundException::new);

        boolean registerFlag = cardConnectionRepository.existsById(
                new CardConnectionId(card.getId(), targetCard.getId()));

        return new ResponseCardDto(targetCard, !registerFlag);
    }

    public ResponseCardDto getMyCard(UUID eventId, Long accountId) {
        Card card = cardRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow(CardNotFoundException::new);

        return new ResponseCardDto(card, false);
    }

    public ResponseParticipantFlagDto isJoined(UUID eventId, Long accountId) {
        Optional<Card> profileOptional = cardRepository.findByEventIdAndAccountId(eventId, accountId);

        return new ResponseParticipantFlagDto(profileOptional.isPresent());
    }

    public boolean hasCompletedCard(Account account, UUID eventId) {
        Long accountId = account.getId();
        Optional<Card> optionalProfile = cardRepository.findByEventIdAndAccountId(eventId, accountId);

        if (optionalProfile.isEmpty()) {
            throw new CardNotFoundException();
        }

        Card card = optionalProfile.get();

        if (card.isCompleted()) {
            return true;
        }

        throw new CardUncompletedException();
    }
}
