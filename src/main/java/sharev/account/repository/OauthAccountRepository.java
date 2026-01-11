package sharev.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sharev.account.entity.OauthAccount;
import sharev.account.entity.OauthAccount.OauthAccountId;

public interface OauthAccountRepository extends JpaRepository<OauthAccount, OauthAccountId> {
}
