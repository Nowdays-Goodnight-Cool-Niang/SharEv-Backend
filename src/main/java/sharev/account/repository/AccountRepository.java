package sharev.account.repository;

import java.util.Optional;
import sharev.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByKakaoOauthId(Long kakaoOauthId);
}
