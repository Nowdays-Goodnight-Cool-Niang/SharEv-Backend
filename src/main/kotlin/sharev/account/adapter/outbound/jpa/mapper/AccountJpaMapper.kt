package sharev.account.adapter.outbound.jpa.mapper

import sharev.account.adapter.outbound.jpa.entity.AccountJpaEntity
import sharev.account.domain.model.Account

fun Account.toJpaEntity(): AccountJpaEntity {
    return AccountJpaEntity(
        id.takeIf { it != 0L },
        name,
        email,
        role
    )
}

fun AccountJpaEntity.toDomainModel(): Account = Account(id!!, name, email, role)

fun AccountJpaEntity.updateFrom(domain: Account) {
    name = domain.name
    email = domain.email
    role = domain.role
}
