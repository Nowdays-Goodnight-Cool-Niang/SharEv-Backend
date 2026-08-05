package sharev.member.adapter.outbound.jpa.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import sharev.member.adapter.outbound.jpa.entity.MemberJpaEntity
import sharev.member.domain.model.MemberRole
import sharev.team.adapter.outbound.jpa.entity.TeamJpaEntity

interface MemberRepository : JpaRepository<MemberJpaEntity, Long> {

    fun findByTeamAndAccountId(team: TeamJpaEntity, accountId: Long): MemberJpaEntity?

    fun existsByTeamIdAndAccountId(teamId: Long, accountId: Long): Boolean

    @EntityGraph(attributePaths = ["account"])
    fun findAllByTeam(team: TeamJpaEntity): List<MemberJpaEntity>

    fun countByTeamAndRole(team: TeamJpaEntity, role: MemberRole): Long
}
