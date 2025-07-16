package org.example.backend.social_dex.repository;

import java.time.LocalDateTime;
import java.util.UUID;
import org.example.backend.participant.dto.response.ParticipantProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SocialDexRepositoryCustom {
    Long getRegisterCount(UUID eventId, Long eventProfileId, LocalDateTime snapshotTime);

    Page<ParticipantProfile> findSocialDexParticipants(UUID eventId, Long eventProfileId,
                                                       LocalDateTime snapshotTime,
                                                       Pageable pageable);
}
