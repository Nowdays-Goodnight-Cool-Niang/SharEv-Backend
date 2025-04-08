package org.example.backend.socialDex.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public interface ResponseSocialDexInfoDto {

    record AccountInfo(String name, String email, String linkedinUrl, String githubUrl, String instagramUrl,
                       String teamName, String position, String introductionText, boolean registerFlag)
            implements ResponseSocialDexInfoDto {
        @QueryProjection
        public AccountInfo {
        }

        public ResponseSocialDexInfoDto convertSocialDexInfo() {
            if (registerFlag) {
                return this;
            }

            return new NotRegisteredAccountInfo(name, false);
        }

        public record NotRegisteredAccountInfo(String name, boolean registerFlag) implements ResponseSocialDexInfoDto {
        }
    }
}



