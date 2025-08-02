package org.example.backend.account.repository;

import java.util.Optional;
import org.example.backend.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByKakaoOauthId(Long kakaoOauthId);
}
