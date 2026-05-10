package sharev.link.adapter.outbound.jpa.mapper

import sharev.link.adapter.outbound.jpa.entity.LinkJpaEntity
import sharev.link.domain.model.Link

fun LinkJpaEntity.toDomainModel() = Link(
    id = id!!,
    accountId = account.id!!,
    url = linkUrl,
)
