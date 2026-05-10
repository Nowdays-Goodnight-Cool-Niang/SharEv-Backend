package sharev.card.adapter.outbound.jpa.mapper

import sharev.card.adapter.outbound.jpa.entity.CardJpaEntity
import sharev.card.domain.model.Card

fun CardJpaEntity.toDomainModel() = Card(
    id = id!!,
    gatheringId = gathering.id!!,
    accountId = account.id!!,
    accountName = account.name,
    accountEmail = account.email,
    pinNumber = pinNumber,
    templateVersion = templateVersion,
    introductionText = introductionText,
)
