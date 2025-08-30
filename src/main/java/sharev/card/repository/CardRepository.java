package sharev.card.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import sharev.card.dto.response.ResponsePinNumberOnlyDto;
import sharev.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByEventIdAndAccountId(UUID eventId, Long accountId);

    Optional<Card> findByEventIdAndPinNumber(UUID eventId, Integer pinNumber);

    Set<ResponsePinNumberOnlyDto> findAllByEventId(UUID eventId);
}
