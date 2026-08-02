package sharev.team.adapter.outbound.jpa.mapper

import sharev.team.adapter.outbound.jpa.entity.TeamJpaEntity
import sharev.team.domain.model.Team

fun TeamJpaEntity.toDomainModel() = Team(
    id = checkNotNull(id),
    teamCertification = certification,
    title = title,
    content = content,
    teamType = type,
    createdAt = createdAt,
)
