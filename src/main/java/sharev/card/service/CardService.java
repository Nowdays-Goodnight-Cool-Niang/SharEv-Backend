package sharev.card.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sharev.account.entity.Account;
import sharev.account.entity.Link;
import sharev.account.exception.AccountNotFoundException;
import sharev.account.repository.AccountRepository;
import sharev.account.repository.LinkRepository;
import sharev.card.dto.response.ResponseCardDto;
import sharev.card.dto.response.ResponseParticipantFlagDto;
import sharev.card.dto.response.ResponsePinNumberOnlyDto;
import sharev.card.dto.response.ResponseUpdateCardInfoDto;
import sharev.card.dto.response.TempResponseCardDto;
import sharev.card.entity.Card;
import sharev.card.exception.CardNotFoundException;
import sharev.card.exception.CardUncompletedException;
import sharev.card.exception.JoinAlreadyException;
import sharev.card.exception.KeyBlankException;
import sharev.card.exception.PinNumberGenerateException;
import sharev.card.repository.CardRepository;
import sharev.connection.event.ShowCardEvent;
import sharev.gathering.entity.Gathering;
import sharev.gathering.entity.IntroduceTemplate;
import sharev.gathering.exception.GatheringNotFoundException;
import sharev.gathering.exception.IntroduceTemplateNotFoundException;
import sharev.gathering.repository.GatheringRepository;
import sharev.gathering.repository.IntroduceTemplateRepository;
import sharev.gathering.util.GatheringKeyGenerator;
import sharev.util.LockProcessor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {
    private static final int START_PIN_RANGE = 1_000;
    private static final int END_PIN_RANGE = 9_999;
    private static final Long EXPIRE_PIN_NUMBER_DAY = 3L;

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final GatheringRepository gatheringRepository;
    private final IntroduceTemplateRepository introduceTemplateRepository;
    private final LinkRepository linkRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LockProcessor lockProcessor;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void join(UUID gatheringId, Long accountId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(GatheringNotFoundException::new);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        int pinNumber = getPinNumber(gatheringId);

        try {
            cardRepository.save(new Card(gathering, account, pinNumber));
        } catch (DataIntegrityViolationException e) {
            String eventPinKey = GatheringKeyGenerator.calculateEventPinKey(gatheringId);
            redisTemplate.opsForSet().add(eventPinKey, pinNumber);
            throw new JoinAlreadyException();
        }
    }

    private int getPinNumber(UUID gatheringId) {
        Integer pinNumber = getUniquePinNumber(gatheringId);
        if (Objects.isNull(pinNumber)) {
            throw new PinNumberGenerateException();
        }

        return pinNumber;
    }

    private Integer getUniquePinNumber(UUID gatheringId) {
        String eventPinKey = GatheringKeyGenerator.calculateEventPinKey(gatheringId);

        Integer pinNumber = (Integer) redisTemplate.opsForSet().pop(eventPinKey);

        if (Objects.nonNull(pinNumber)) {
            return pinNumber;
        }

        lockProcessor.lock(eventPinKey, key -> insertPinNumbers(gatheringId, key));

        return (Integer) redisTemplate.opsForSet().pop(eventPinKey);
    }

    private void insertPinNumbers(UUID gatheringId, String eventPinKey) {
        Boolean keyExistFlag = redisTemplate.hasKey(eventPinKey);

        if (Objects.isNull(keyExistFlag)) {
            throw new KeyBlankException();
        }

        if (keyExistFlag.equals(Boolean.TRUE)) {
            return;
        }

        Set<Integer> usingPinNumbers = cardRepository.findAllByGatheringId(gatheringId).stream()
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
    public ResponseUpdateCardInfoDto updateInfo(UUID gatheringId, Long accountId, Integer templateVersion,
                                                Map<String, String> introductionText) {
        Card card = cardRepository.findByGatheringIdAndAccountId(gatheringId, accountId)
                .orElseThrow(CardNotFoundException::new);

        IntroduceTemplate introduceTemplate =
                introduceTemplateRepository.findByGatheringIdAndVersion(gatheringId, templateVersion)
                        .orElseThrow(IntroduceTemplateNotFoundException::new);

        card.updateIntroductionText(introduceTemplate, templateVersion, introductionText);

        return new ResponseUpdateCardInfoDto(templateVersion, introductionText);
    }

    public ResponseCardDto getCardByPinNumber(UUID gatheringId, Long accountId, Integer pinNumber) {
        Card targetCard = cardRepository.findByGatheringIdAndPinNumber(gatheringId, pinNumber)
                .orElseThrow(CardNotFoundException::new);

        applicationEventPublisher.publishEvent(new ShowCardEvent(gatheringId, accountId, targetCard.getId()));

        return calculateResponseCardDto(gatheringId, targetCard);
    }

    public ResponseCardDto getMyCard(UUID gatheringId, Long accountId) {
        Card card = cardRepository.findByGatheringIdAndAccountId(gatheringId, accountId)
                .orElseThrow(CardNotFoundException::new);

        return calculateResponseCardDto(gatheringId, card);
    }

    private ResponseCardDto calculateResponseCardDto(UUID gatheringId, Card card) {
        Account account = card.getAccount();
        List<String> linkUrls = account.getLinks().stream()
                .map(Link::getLinkUrl)
                .toList();

        IntroduceTemplate lastintroduceTemplate =
                introduceTemplateRepository.findTopByGatheringIdOrderByVersionDesc(gatheringId)
                        .orElseThrow(IntroduceTemplateNotFoundException::new);

        Integer templateVersion = card.getTemplateVersion();

        IntroduceTemplate introduceTemplate =
                introduceTemplateRepository.findByGatheringIdAndVersion(gatheringId, templateVersion)
                        .orElseThrow(IntroduceTemplateNotFoundException::new);

        return new ResponseCardDto(card, linkUrls, account, lastintroduceTemplate.getVersion(),
                introduceTemplate);
    }

    public ResponseParticipantFlagDto isJoined(UUID gatheringId, Long accountId) {
        Optional<Card> profileOptional = cardRepository.findByGatheringIdAndAccountId(gatheringId, accountId);

        return new ResponseParticipantFlagDto(profileOptional.isPresent());
    }

    public boolean hasCompletedCard(Account account, UUID gatheringId) {
        Long accountId = account.getId();
        Card card = cardRepository.findByGatheringIdAndAccountId(gatheringId, accountId)
                .orElseThrow(CardNotFoundException::new);

        if (card.isCompleted()) {
            return true;
        }

        throw new CardUncompletedException();
    }

    public Page<ResponseCardDto> getAllCard(UUID gatheringId, Long accountId, LocalDateTime snapshotTime,
                                            Pageable pageable) {

        Page<TempResponseCardDto> tempResponseCards = cardRepository.searchTempCards(gatheringId, accountId,
                snapshotTime, pageable);

        IntroduceTemplate introduceTemplate = introduceTemplateRepository.findTopByGatheringIdOrderByVersionDesc(
                        gatheringId)
                .orElseThrow(IntroduceTemplateNotFoundException::new);

        Integer lastVersion = introduceTemplate.getVersion();

        List<Long> accountIds = tempResponseCards.getContent().stream()
                .map(temp -> temp.account().getId())
                .distinct()
                .toList();

        Map<Long, List<Link>> accountLinks = linkRepository.findAllByAccountIdIn(accountIds)
                .stream()
                .collect(Collectors.groupingBy(link -> link.getAccount().getId()));

        return tempResponseCards.map(temp ->
                temp.toResponseDto(accountLinks.get(temp.account().getId()), lastVersion));
    }
}
