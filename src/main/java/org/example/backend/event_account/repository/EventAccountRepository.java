package org.example.backend.event_account.repository;


import java.util.Optional;
import org.example.backend.event_account.entity.EventAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventAccountRepository extends JpaRepository<EventAccount, Long> {
    Optional<EventAccount> findByEventIdAndAccountId(Long eventId, Long accountId);
}
