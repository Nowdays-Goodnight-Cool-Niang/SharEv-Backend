package org.example.backend.relation.repository;

import java.time.LocalDateTime;
import java.util.UUID;
import org.example.backend.relation.dto.response.RelationProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RelationRepositoryCustom {
    Long getRegisterCount(UUID eventId, Long profileId, LocalDateTime snapshotTime);

    Page<RelationProfileDto> findRelationProfiles(UUID eventId, Long profileId,
                                                  LocalDateTime snapshotTime,
                                                  Pageable pageable);
}
