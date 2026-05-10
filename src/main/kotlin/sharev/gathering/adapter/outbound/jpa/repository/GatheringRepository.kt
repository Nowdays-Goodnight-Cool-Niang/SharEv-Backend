package sharev.gathering.adapter.outbound.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import sharev.gathering.adapter.outbound.jpa.entity.GatheringJpaEntity
import sharev.team.adapter.outbound.jpa.entity.TeamJpaEntity
import java.util.*

interface GatheringRepository : JpaRepository<GatheringJpaEntity, UUID> {
    fun findAllByTeam(team: TeamJpaEntity): List<GatheringJpaEntity>
}
