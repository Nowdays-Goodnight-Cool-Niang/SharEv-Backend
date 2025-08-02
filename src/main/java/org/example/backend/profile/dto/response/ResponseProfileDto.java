package org.example.backend.profile.dto.response;

import org.example.backend.profile.entity.Profile;
import org.example.backend.util.Type;

public record ResponseProfileDto(Type type, Long profileId, String name, String email, String linkedinUrl,
                                 String githubUrl,
                                 String instagramUrl,
                                 int iconNumber,
                                 int pinNumber,
                                 String introduce,
                                 String proudestExperience,
                                 String toughExperience,
                                 boolean registerRequireFlag) {

    public ResponseProfileDto(Long profileId, String name, String email, String linkedinUrl,
                              String githubUrl,
                              String instagramUrl, int iconNumber, int pinNumber, String introduce,
                              String proudestExperience, String toughExperience, boolean registerRequireFlag) {

        this(Type.FULL, profileId, name, email, linkedinUrl, githubUrl, instagramUrl, iconNumber, pinNumber, introduce,
                proudestExperience, toughExperience, registerRequireFlag);
    }

    public ResponseProfileDto(Profile profile, boolean registerRequireFlag) {
        this(Type.FULL, profile.getId(), profile.getAccount().getName(), profile.getAccount().getEmail(),
                profile.getAccount().getLinkedinUrl(), profile.getAccount().getGithubUrl(),
                profile.getAccount().getInstagramUrl(), profile.getIconNumber(), profile.getPinNumber(),
                profile.getIntroduce(), profile.getProudestExperience(), profile.getToughExperience(),
                registerRequireFlag);
    }
}
