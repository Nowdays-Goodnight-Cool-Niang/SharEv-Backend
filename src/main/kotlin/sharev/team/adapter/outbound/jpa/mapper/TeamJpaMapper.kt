package sharev.team.adapter.outbound.jpa.mapper

import sharev.team.adapter.outbound.jpa.entity.TeamJpaEntity
import sharev.team.domain.model.Team

fun TeamJpaEntity.toDomainModel() = Team(
    id = id!!,
    teamCertification = teamCertification,
    title = title,
    content = content,
    activateFlag = activateFlag,
    createdAt = createdAt,
)
