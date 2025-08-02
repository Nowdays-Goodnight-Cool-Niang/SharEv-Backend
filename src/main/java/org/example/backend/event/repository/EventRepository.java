package org.example.backend.event.repository;

import java.util.UUID;
import org.example.backend.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, UUID> {
}
