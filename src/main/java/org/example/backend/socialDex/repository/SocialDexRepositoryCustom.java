package org.example.backend.socialDex.repository;

import org.example.backend.socialDex.dto.response.ResponseSocialDexInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SocialDexRepositoryCustom {
    Page<ResponseSocialDexInfoDto.AccountInfo> findDexParticipants(UUID accountId, Pageable pageable);
}
