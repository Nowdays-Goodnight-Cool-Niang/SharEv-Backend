package org.example.backend.participant.dto.request;

import java.util.UUID;

public record RequestUpdateParticipantInfoDto(
        UUID eventId,

        String introduce,

        String reminderExperience,

        String wantAgainExperience
) {
}
