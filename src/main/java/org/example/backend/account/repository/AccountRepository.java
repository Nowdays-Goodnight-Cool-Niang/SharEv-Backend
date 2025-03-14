package org.example.backend.account.repository;

import org.example.backend.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByKakaoOauthId(Long kakaoOauthId);
}
