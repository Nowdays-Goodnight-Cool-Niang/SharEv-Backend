package sharev.member.adapter.outbound.jpa.mapper

import sharev.member.adapter.outbound.jpa.entity.MemberJpaEntity
import sharev.member.domain.model.Member

fun MemberJpaEntity.toDomainModel() = Member(
    id = id!!,
    teamId = team.id!!,
    accountId = account.id!!,
    accountName = account.name,
    accountEmail = account.email,
    status = status,
    role = role,
)
