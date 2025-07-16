package org.example.backend.participant.repository;

import java.util.Optional;
import java.util.UUID;
import org.example.backend.participant.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByEventIdAndAccountId(UUID eventId, Long accountId);

    Optional<Participant> findByEventIdAndPinNumber(UUID eventId, Integer pinNumber);
}
