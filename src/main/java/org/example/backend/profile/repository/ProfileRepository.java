package org.example.backend.profile.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.example.backend.profile.dto.response.ResponsePinNumberOnlyDto;
import org.example.backend.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByEventIdAndAccountId(UUID eventId, Long accountId);

    Optional<Profile> findByEventIdAndPinNumber(UUID eventId, Integer pinNumber);

    Set<ResponsePinNumberOnlyDto> findAllByEventId(UUID eventId);
}
