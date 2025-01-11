package org.example.backend.participant.repository;

import org.example.backend.participant.entity.FoundParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoundParticipantRepository extends JpaRepository<FoundParticipant, Long> {

    boolean existsByAccountIdAndParticipantId(Long accountId, Long participantId);

}
