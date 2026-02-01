package sharev.domain.card.repository;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sharev.domain.card.dto.response.TempResponseCardDto;

public interface CardRepositoryCustom {

    Long searchConnectionCount(UUID gatheringId, Long profileId, LocalDateTime snapshotTime);

    Page<TempResponseCardDto> searchTempCards(UUID gatheringId, Long accountId, LocalDateTime snapshotTime,
                                              Pageable pageable);
}
