package sharev.account.adapter.outbound.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import sharev.account.adapter.outbound.jpa.entity.OAuthAccountJpaEntity
import sharev.account.adapter.outbound.jpa.entity.OAuthAccountJpaEntityId

interface OAuthAccountRepository : JpaRepository<OAuthAccountJpaEntity, OAuthAccountJpaEntityId> {
}
