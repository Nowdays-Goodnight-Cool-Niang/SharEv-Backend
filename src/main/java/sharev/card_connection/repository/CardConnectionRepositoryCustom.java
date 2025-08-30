package sharev.card_connection.repository;

import java.time.LocalDateTime;
import java.util.UUID;
import sharev.card_connection.dto.response.ConnectedCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardConnectionRepositoryCustom {
    Long getRegisterCount(UUID eventId, Long profileId, LocalDateTime snapshotTime);

    Page<ConnectedCardDto> findRelationProfiles(UUID eventId, Long profileId,
                                                LocalDateTime snapshotTime,
                                                Pageable pageable);
}
