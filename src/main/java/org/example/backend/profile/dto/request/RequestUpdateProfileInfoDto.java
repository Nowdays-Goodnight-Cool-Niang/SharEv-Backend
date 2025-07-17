package org.example.backend.profile.dto.request;

public record RequestUpdateProfileInfoDto(
        String introduce,

        String proudestExperience,

        String toughExperience
) {
}
