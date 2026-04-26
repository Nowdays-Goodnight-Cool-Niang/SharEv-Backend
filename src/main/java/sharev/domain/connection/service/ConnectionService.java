package sharev.domain.connection.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sharev.domain.card.entity.Card;
import sharev.domain.card.repository.CardRepository;
import sharev.domain.connection.entity.Connection;
import sharev.domain.connection.repository.ConnectionRepository;
import sharev.exception.CustomException;
import sharev.exception.ExceptionCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final CardRepository cardRepository;

    @Transactional
    public void connect(UUID eventId, Long accountId, Long targetCardId) {
        Card card = cardRepository.findByGatheringIdAndAccountId(eventId, accountId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CARD_NOT_FOUND));
        Card targetCard = cardRepository.findById(targetCardId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CARD_NOT_FOUND));

        if (card.getId().equals(targetCard.getId())) {
            throw new CustomException(ExceptionCode.REGISTER_MYSELF);
        }

        List<Connection> connections = Connection.connect(card, targetCard);

        try {
            connectionRepository.saveAllAndFlush(connections);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ExceptionCode.REGISTER_ALREADY);
        }
    }
}
