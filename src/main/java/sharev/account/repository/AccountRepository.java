package sharev.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sharev.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
