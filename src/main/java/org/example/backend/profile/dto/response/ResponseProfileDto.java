package org.example.backend.profile.dto.response;

import org.example.backend.profile.entity.Profile;

public record ResponseProfileDto(Long profileId, String name, String email, String linkedinUrl,
                                 String githubUrl,
                                 String instagramUrl,
                                 String introduce,
                                 String proudestExperience,
                                 String toughExperience,
                                 boolean registerRequireFlag) {

    public ResponseProfileDto(Profile profile, boolean registerRequireFlag) {
        this(profile.getId(), profile.getAccount().getName(), profile.getAccount().getEmail(),
                profile.getAccount().getLinkedinUrl(), profile.getAccount().getGithubUrl(),
                profile.getAccount().getInstagramUrl(), profile.getIntroduce(), profile.getProudestExperience(),
                profile.getToughExperience(), registerRequireFlag);
    }
}
