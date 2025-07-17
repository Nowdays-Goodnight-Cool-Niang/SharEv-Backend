package org.example.backend.profile.dto.request;

import java.util.UUID;

public record RequestUpdateProfileInfoDto(
        UUID eventId,

        String introduce,

        String reminderExperience,

        String wantAgainExperience
) {
}
