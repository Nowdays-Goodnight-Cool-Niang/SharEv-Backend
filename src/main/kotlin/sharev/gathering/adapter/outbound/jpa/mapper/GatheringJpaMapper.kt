package sharev.gathering.adapter.outbound.jpa.mapper

import sharev.gathering.adapter.outbound.jpa.entity.GatheringJpaEntity
import sharev.gathering.adapter.outbound.jpa.entity.IntroduceTemplateJpaEntity
import sharev.gathering.domain.model.Gathering
import sharev.gathering.domain.model.IntroduceTemplate

fun GatheringJpaEntity.toDomainModel() = Gathering(
    id = requireNotNull(id),
    teamId = requireNotNull(team.id),
    visible = visible,
    title = title,
    content = content,
    startAt = startAt,
    endAt = endAt,
    place = place,
    imageUrl = imageUrl,
    gatheringUrl = gatheringUrl,
    contact = contact,
    registerStartAt = registerStartAt,
    registerEndAt = registerEndAt,
)

fun IntroduceTemplateJpaEntity.toDomainModel() = IntroduceTemplate(
    id = requireNotNull(id),
    gatheringId = requireNotNull(gathering.id),
    version = version,
    content = content,
)
