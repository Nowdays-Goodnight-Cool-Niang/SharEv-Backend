package org.example.backend.profile.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import org.example.backend.profile.entity.Profile;

public record ProfileDto(Long participantId, String name, String email, String linkedinUrl,
                         String githubUrl,
                         String instagramUrl,
                         Introduce introduce,
                         boolean registerRequireFlag)
        implements ResponseProfileDto {

    @QueryProjection
    public ProfileDto(Long participantId, String name, String email, String linkedinUrl, String githubUrl,
                      String instagramUrl, String introduce, String reminderExperience, String wantAgainExperience,
                      boolean registerRequireFlag) {
        this(participantId, name, email, linkedinUrl, githubUrl, instagramUrl,
                new Introduce(introduce, reminderExperience, wantAgainExperience), registerRequireFlag);
    }

    public ProfileDto(Profile profile, boolean registerRequireFlag) {
        this(profile.getId(), profile.getAccount().getName(), profile.getAccount().getEmail(),
                profile.getAccount().getLinkedinUrl(), profile.getAccount().getGithubUrl(),
                profile.getAccount().getInstagramUrl(), new Introduce(profile.getIntroduce(),
                        profile.getReminderExperience(), profile.getWantAgainExperience()), registerRequireFlag);
    }

    public ResponseProfileDto convertResponseProfileDto() {
        if (registerRequireFlag) {
            return this;
        }

        return new UnknownProfileDto(name, false);
    }

    public record UnknownProfileDto(String name, boolean registerFlag) implements ResponseProfileDto {
    }
}

record Introduce(Long version, String introduce, String reminderExperience, String wantAgainExperience) {
    Introduce(String introduce, String reminderExperience, String wantAgainExperience) {
        this(1L, introduce, reminderExperience, wantAgainExperience);
    }
}
