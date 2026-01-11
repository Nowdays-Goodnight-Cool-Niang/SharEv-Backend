package sharev.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sharev.domain.account.entity.OauthAccount;
import sharev.domain.account.entity.OauthAccount.OauthAccountId;

public interface OauthAccountRepository extends JpaRepository<OauthAccount, OauthAccountId> {
}
