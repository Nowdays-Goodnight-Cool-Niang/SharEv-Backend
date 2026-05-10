package sharev.member.adapter.outbound.jpa.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import sharev.account.adapter.outbound.jpa.entity.AccountJpaEntity
import sharev.member.adapter.outbound.jpa.entity.MemberJpaEntity
import sharev.member.domain.model.MemberRole
import sharev.team.adapter.outbound.jpa.entity.TeamJpaEntity

interface MemberRepository : JpaRepository<MemberJpaEntity, Long> {
    fun findAllByAccount(account: AccountJpaEntity): List<MemberJpaEntity>

    fun findByTeamAndAccount(team: TeamJpaEntity, account: AccountJpaEntity): MemberJpaEntity?

    fun findByTeamAndAccountId(team: TeamJpaEntity, accountId: Long): MemberJpaEntity?

    @EntityGraph(attributePaths = ["account"])
    fun findAllByTeam(team: TeamJpaEntity): List<MemberJpaEntity>

    fun findByTeamAndAccountEmail(team: TeamJpaEntity, email: String): MemberJpaEntity?

    fun countByTeamAndRole(team: TeamJpaEntity, role: MemberRole): Long
}
