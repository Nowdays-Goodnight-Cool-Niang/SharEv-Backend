package org.example.backend.participant.repository;

import org.example.backend.participant.dto.response.ResponseGetParticipantInfo;

public interface ParticipantRepositoryExtension {

    ResponseGetParticipantInfo fetchDetailsBy(Long id);

}
