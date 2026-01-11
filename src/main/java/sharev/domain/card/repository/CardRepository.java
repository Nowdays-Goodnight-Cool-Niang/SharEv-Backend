package sharev.domain.card.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import sharev.domain.card.dto.response.ResponsePinNumberOnlyDto;
import sharev.domain.card.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long>, CardRepositoryCustom {

    Optional<Card> findByGatheringIdAndAccountId(UUID gatheringId, Long accountId);

    Optional<Card> findByGatheringIdAndPinNumber(UUID gatheringId, Integer pinNumber);

    Set<ResponsePinNumberOnlyDto> findAllByGatheringId(UUID gatheringId);
}
