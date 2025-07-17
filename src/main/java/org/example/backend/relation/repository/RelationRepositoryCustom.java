package org.example.backend.relation.repository;

import java.time.LocalDateTime;
import java.util.UUID;
import org.example.backend.profile.dto.response.ProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RelationRepositoryCustom {
    Long getRegisterCount(UUID eventId, Long profileId, LocalDateTime snapshotTime);

    Page<ProfileDto> findRelationProfiles(UUID eventId, Long profileId,
                                          LocalDateTime snapshotTime,
                                          Pageable pageable);
}
