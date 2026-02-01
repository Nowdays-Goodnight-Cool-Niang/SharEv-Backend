package sharev.domain.connection.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sharev.domain.card.entity.Card;
import sharev.domain.card.exception.CardNotFoundException;
import sharev.domain.card.repository.CardRepository;
import sharev.domain.connection.entity.Connection;
import sharev.domain.connection.exception.RegisterAlreadyException;
import sharev.domain.connection.exception.RegisterMyselfException;
import sharev.domain.connection.repository.ConnectionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final CardRepository cardRepository;

    @Transactional
    public void connect(UUID eventId, Long accountId, Long targetCardId) {
        Card card = cardRepository.findByGatheringIdAndAccountId(eventId, accountId)
                .orElseThrow(CardNotFoundException::new);
        Card targetCard = cardRepository.findById(targetCardId)
                .orElseThrow(CardNotFoundException::new);

        if (card.getId().equals(targetCard.getId())) {
            throw new RegisterMyselfException();
        }

        List<Connection> connections = Connection.connect(card, targetCard);

        try {
            connectionRepository.saveAllAndFlush(connections);
        } catch (DataIntegrityViolationException e) {
            throw new RegisterAlreadyException();
        }
    }
}
