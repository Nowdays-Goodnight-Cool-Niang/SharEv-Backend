package org.example.backend.socialDex.repository;

import org.example.backend.socialDex.dto.response.ResponseSocialDexInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SocialDexRepositoryCustom {
    Long getRegisterCount(UUID accountId, LocalDateTime snapshotTime);

    Page<ResponseSocialDexInfoDto.AccountInfo> findDexParticipants(UUID accountId, LocalDateTime snapshotTime, Pageable pageable);
}
