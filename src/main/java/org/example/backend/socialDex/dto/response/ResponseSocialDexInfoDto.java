package org.example.backend.socialDex.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import org.springframework.data.domain.Page;

import java.util.UUID;

public record ResponseSocialDexInfoDto(Long registerCount, Page<SocialDexInfo> socialDexInfo) {

    public interface SocialDexInfo {
    }

    public record AccountInfo(UUID id, String name, String email, String linkedinUrl, String githubUrl,
                              String instagramUrl,
                              String teamName, String position, String introductionText, boolean registerFlag)
            implements SocialDexInfo {
        @QueryProjection
        public AccountInfo {
        }

        public SocialDexInfo convertSocialDexInfo() {
            if (registerFlag) {
                return this;
            }

            return new NotRegisteredAccountInfo(id, name, false);
        }

        public record NotRegisteredAccountInfo(UUID id, String name, boolean registerFlag) implements SocialDexInfo {
        }
    }
}



