package sharev.connection.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sharev.card.entity.Card;
import sharev.card.exception.CardNotFoundException;
import sharev.card.repository.CardRepository;
import sharev.connection.entity.Connection;
import sharev.connection.exception.RegisterAlreadyException;
import sharev.connection.exception.RegisterMyselfException;
import sharev.connection.repository.ConnectionRepository;

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
