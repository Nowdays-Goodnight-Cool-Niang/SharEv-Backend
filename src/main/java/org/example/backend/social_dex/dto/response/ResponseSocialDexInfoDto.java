package org.example.backend.social_dex.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import org.springframework.data.domain.Page;

public record ResponseSocialDexInfoDto(Long registerCount, Page<SocialDexInfo> socialDexInfo) {

    public interface SocialDexInfo {
    }

    public record ParticipantInfo(Long participantId, String name, String email, String linkedinUrl, String githubUrl,
                                  String instagramUrl,
                                  String introduce, String reminderExperience, String wantAgainExperience,
                                  boolean registerFlag)
            implements SocialDexInfo {

        @QueryProjection
        public ParticipantInfo {
            // QueryProjection 명시를 위해 내용이 없는 생성자 구성
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



