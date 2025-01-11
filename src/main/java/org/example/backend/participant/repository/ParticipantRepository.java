package org.example.backend.participant.repository;


import java.util.Optional;
import org.example.backend.participant.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long>, ParticipantRepositoryExtension {
    Optional<Participant> findByEventIdAndAccountId(Long eventId, Long accountId);
}
