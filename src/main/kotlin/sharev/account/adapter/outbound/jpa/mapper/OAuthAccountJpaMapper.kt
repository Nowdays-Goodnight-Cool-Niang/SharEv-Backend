package sharev.account.adapter.outbound.jpa.mapper

import sharev.account.adapter.outbound.jpa.entity.AccountJpaEntity
import sharev.account.adapter.outbound.jpa.entity.OAuthAccountJpaEntity
import sharev.account.domain.model.OAuthAccount

fun OAuthAccountJpaEntity.toDomainModel() = OAuthAccount(provider, subjectIdentifier, account.id!!)
fun OAuthAccount.toJpaEntity(accountJpaEntity: AccountJpaEntity) =
    OAuthAccountJpaEntity(provider, subjectIdentifier, accountJpaEntity)
