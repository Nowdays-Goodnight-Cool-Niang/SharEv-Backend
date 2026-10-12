package sharev.team.adapter.outbound.jpa.mapper

import sharev.team.adapter.outbound.jpa.entity.TeamJpaEntity
import sharev.team.domain.model.Team

fun TeamJpaEntity.toDomainModel(): Team = Team(checkNotNull(id), title, content, certification, createdAt)

fun Team.toJpaEntity(): TeamJpaEntity = TeamJpaEntity(id.takeIf { it != 0L }, title, content, certification)
