package org.example.backend.profile.dto.request;

public record RequestUpdateProfileInfoDto(
        String introduce,

        String reminderExperience,

        String wantAgainExperience
) {
}
