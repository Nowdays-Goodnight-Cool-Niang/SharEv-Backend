package org.example.backend.social_dex.repository;

import java.time.LocalDateTime;
import java.util.UUID;
import org.example.backend.social_dex.dto.response.ResponseSocialDexInfoDto.ParticipantInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SocialDexRepositoryCustom {
    Long getRegisterCount(UUID eventId, Long eventProfileId, LocalDateTime snapshotTime);

    Page<ParticipantInfo> findDexParticipants(UUID eventId, Long eventProfileId, LocalDateTime snapshotTime,
                                              Pageable pageable);
}
