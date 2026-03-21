package sharev.domain.account.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sharev.domain.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
}
