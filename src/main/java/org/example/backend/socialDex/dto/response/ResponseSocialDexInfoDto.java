package org.example.backend.socialDex.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import org.springframework.data.domain.Page;

public record ResponseSocialDexInfoDto(Long totalSize, Page<AccountInfo> accountInfoPage) {

    public record AccountInfo(String email, String linkedinUrl, String githubUrl, String instagramUrl,
                              String teamName, String position, String introductionText) {
        @QueryProjection
        public AccountInfo {
        }
    }
}
