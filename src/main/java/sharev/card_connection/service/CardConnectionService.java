package sharev.card_connection.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import sharev.card.entity.Card;
import sharev.card.exception.CardNotFoundException;
import sharev.card.repository.CardRepository;
import sharev.card_connection.dto.response.ConnectedCardDto;
import sharev.card_connection.dto.response.ResponseConnectionCardDto;
import sharev.card_connection.dto.response.ResponseConnectionInfoDto;
import sharev.card_connection.entity.CardConnection;
import sharev.card_connection.exception.RegisterAlreadyException;
import sharev.card_connection.exception.RegisterMyselfException;
import sharev.card_connection.repository.CardConnectionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardConnectionService {

    private final CardConnectionRepository cardConnectionRepository;
    private final CardRepository cardRepository;

    @Transactional
    public void connect(UUID eventId, Long accountId, Integer targetPinNumber) {
        Card card = cardRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow(CardNotFoundException::new);
        Card targetCard = cardRepository.findByEventIdAndPinNumber(eventId, targetPinNumber)
                .orElseThrow(CardNotFoundException::new);

        if (card.getId().equals(targetCard.getId())) {
            throw new RegisterMyselfException();
        }

        List<CardConnection> cardConnection = CardConnection.connect(card, targetCard);

        try {
            cardConnectionRepository.saveAllAndFlush(cardConnection);
        } catch (DataIntegrityViolationException e) {
            throw new RegisterAlreadyException();
        }
    }

    public ResponseConnectionInfoDto getConnectionInfos(UUID eventId, Long accountId, LocalDateTime snapshotTime,
                                                        Pageable pageable) {
        Card card = cardRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow(CardNotFoundException::new);

        Long registerCount = cardConnectionRepository.getRegisterCount(eventId, card.getId(), snapshotTime);
        Page<ConnectedCardDto> relationProfiles =
                cardConnectionRepository.findRelationProfiles(eventId, card.getId(), snapshotTime, pageable);
        Page<ResponseConnectionCardDto> responseRelationProfiles =
                relationProfiles.map(ConnectedCardDto::convertIfNonRelation);

        return new ResponseConnectionInfoDto(registerCount, responseRelationProfiles);
    }
}
