package sharev.team.adapter.outbound.jpa.repository

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import sharev.team.application.port.outbound.summery.TeamMemberSummary
import sharev.team.application.port.outbound.summery.TeamSummary
import java.time.LocalDateTime

@Repository
class TeamRepositoryImpl(
    private val entityManager: EntityManager,
) : TeamRepositoryCustom {

    override fun findMyTeams(accountId: Long): List<TeamSummary> {
        val rows = entityManager.createQuery(
            """
            select t.id, t.title, t.content, t.createdAt, m.role,
                   (select count(m2.id) from MemberJpaEntity m2 where m2.team = t)
            from MemberJpaEntity m
            join m.team t
            where m.account.id = :accountId
            """.trimIndent(),
            Array<Any>::class.java
        )
            .setParameter("accountId", accountId)
            .resultList

        return rows.map { row ->
            TeamSummary(
                id = row[0] as Long,
                title = row[1] as String,
                content = row[2] as String?,
                createdAt = row[3] as LocalDateTime?,
                memberRole = row[4] as String,
                headcount = (row[5] as Long).toInt(),
            )
        }
    }

    override fun findMyTeamMembers(teamId: Long): List<TeamMemberSummary> {
        val rows = entityManager.createQuery(
            """
            select m.account.name, m.account.email
            from MemberJpaEntity m
            where m.team.id = :teamId
            """.trimIndent(),
            Array<Any>::class.java
        )
            .setParameter("teamId", teamId)
            .resultList

        return rows.map { row ->
            TeamMemberSummary(
                name = row[0] as String,
                email = row[1] as String,
            )
        }
    }
}
