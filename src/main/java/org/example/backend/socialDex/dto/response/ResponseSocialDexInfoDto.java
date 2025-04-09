package org.example.backend.socialDex.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import org.springframework.data.domain.Page;

public record ResponseSocialDexInfoDto(Long registerCount, Page<SocialDexInfo> socialDexInfo) {

    public interface SocialDexInfo {
    }

    public record AccountInfo(String name, String email, String linkedinUrl, String githubUrl, String instagramUrl,
                              String teamName, String position, String introductionText, boolean registerFlag)
            implements SocialDexInfo {
        @QueryProjection
        public AccountInfo {
        }

        public SocialDexInfo convertSocialDexInfo() {
            if (registerFlag) {
                return this;
            }

            return new NotRegisteredAccountInfo(name, false);
        }

        public record NotRegisteredAccountInfo(String name, boolean registerFlag) implements SocialDexInfo {
        }
    }
}



