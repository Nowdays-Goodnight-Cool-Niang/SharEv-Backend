package sharev.member.adapter.outbound.jpa.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sharev.member.adapter.outbound.jpa.entity.MemberJpaEntity
import sharev.member.domain.model.MemberRole
import sharev.member.domain.model.MemberStatus
import sharev.team.adapter.outbound.jpa.entity.TeamJpaEntity

interface MemberRepository : JpaRepository<MemberJpaEntity, Long> {

    fun findByTeamAndAccountId(team: TeamJpaEntity, accountId: Long): MemberJpaEntity?

    fun existsByTeamIdAndAccountId(teamId: Long, accountId: Long): Boolean

    @EntityGraph(attributePaths = ["account"])
    fun findAllByTeam(team: TeamJpaEntity): List<MemberJpaEntity>

    @EntityGraph(attributePaths = ["account"])
    fun findAllByTeamId(teamId: Long): List<MemberJpaEntity>

    fun countByTeamAndRole(team: TeamJpaEntity, role: MemberRole): Long

    @Query(
        """
      select m.team.id as teamId, count(m) as headcount
      from MemberJpaEntity m
      where m.team.id in :teamIds
      group by m.team.id
    """
    )
    fun countGroupByTeamId(teamIds: Collection<Long>): List<TeamHeadcount>

    fun findAllByAccountIdAndRoleAndStatus(
        accountId: Long,
        role: MemberRole,
        status: MemberStatus
    ): List<MemberJpaEntity>

    fun findAllByAccountId(accountId: Long, pageable: Pageable): Page<MemberJpaEntity>
}

interface TeamHeadcount {
    val teamId: Long
    val headcount: Long
}
