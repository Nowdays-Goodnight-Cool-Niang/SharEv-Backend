package sharev.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sharev.domain.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
