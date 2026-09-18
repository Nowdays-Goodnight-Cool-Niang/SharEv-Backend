package sharev.gathering.adapter.outbound.jpa.mapper

import sharev.gathering.adapter.outbound.jpa.entity.GatheringJpaEntity
import sharev.gathering.adapter.outbound.jpa.entity.IntroductionJpaEntity
import sharev.gathering.domain.model.Gathering
import sharev.gathering.domain.model.Introduction
import sharev.gathering.domain.model.Template

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

fun IntroductionJpaEntity.toDomainModel() = Introduction(
    id = requireNotNull(id),
    gatheringId = requireNotNull(gathering.id),
    version = version,
    template = Template(source, fields),
)
