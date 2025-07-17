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
                      String instagramUrl, String introduce, String proudestExperience, String toughExperience,
                      boolean registerRequireFlag) {
        this(participantId, name, email, linkedinUrl, githubUrl, instagramUrl,
                new Introduce(introduce, proudestExperience, toughExperience), registerRequireFlag);
    }

    public ProfileDto(Profile profile, boolean registerRequireFlag) {
        this(profile.getId(), profile.getAccount().getName(), profile.getAccount().getEmail(),
                profile.getAccount().getLinkedinUrl(), profile.getAccount().getGithubUrl(),
                profile.getAccount().getInstagramUrl(), new Introduce(profile.getIntroduce(),
                        profile.getProudestExperience(), profile.getToughExperience()), registerRequireFlag);
    }

    public ResponseProfileDto convertResponseProfileDto() {
        if (registerRequireFlag) {
            return this;
        }

        return new UnknownProfileDto(name);
    }

    public record UnknownProfileDto(String name) implements ResponseProfileDto {
    }
}

record Introduce(Long version, String introduce, String proudestExperience, String toughExperience) {
    Introduce(String introduce, String proudestExperience, String toughExperience) {
        this(1L, introduce, proudestExperience, toughExperience);
    }
}
