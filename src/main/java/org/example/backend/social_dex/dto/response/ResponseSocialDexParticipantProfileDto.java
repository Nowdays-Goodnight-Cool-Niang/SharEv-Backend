package org.example.backend.social_dex.dto.response;

import org.example.backend.participant.dto.response.ParticipantProfileDto;
import org.springframework.data.domain.Page;

public record ResponseSocialDexParticipantProfileDto(
        Long registerCount,
        Page<ParticipantProfileDto> participantProfiles) {
}



