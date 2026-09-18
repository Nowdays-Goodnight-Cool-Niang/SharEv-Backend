package sharev.gathering.adapter.outbound.jpa.repository

import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import sharev.gathering.application.port.outbound.LoadGatheringFilter
import sharev.gathering.application.port.outbound.summary.GatheringDetailSummary
import sharev.gathering.domain.model.GatheringVisible
import sharev.gathering.domain.model.PeriodStatus
import sharev.member.domain.model.MemberRole
import sharev.team.domain.model.TeamType
import java.time.LocalDateTime

@Repository
class GatheringRepositoryImpl(
    private val entityManager: EntityManager,
) : GatheringRepositoryCustom {

    override fun searchGatheringDetails(filter: LoadGatheringFilter, pageable: Pageable): Page<GatheringDetailSummary> {
        val now = LocalDateTime.now()
        val where = buildWhere(filter, now)

        val content = entityManager.createQuery(
            """
            SELECT new sharev.gathering.application.port.outbound.summary.GatheringDetailSummary(
                gathering.id,
                gathering.team.id,
                gathering.team.title,
                (
                    SELECT member.account.handle FROM MemberJpaEntity member
                    WHERE member.team = gathering.team AND member.role = :adminRole AND member.team.type = :personalType
                ),
                gathering.visible,
                gathering.title,
                gathering.content,
                gathering.startAt,
                gathering.endAt,
                gathering.place,
                gathering.imageUrl,
                gathering.gatheringUrl,
                gathering.contact,
                gathering.registerStartAt,
                gathering.registerEndAt
            )
            FROM GatheringJpaEntity gathering
            WHERE 1 = 1${where.clause}
            ${orderBy(pageable.sort)}
            """.trimIndent(),
            GatheringDetailSummary::class.java,
        ).apply {
            setParameter("adminRole", MemberRole.ADMIN)
            setParameter("personalType", TeamType.PERSONAL)
            where.params.forEach { (key, value) -> setParameter(key, value) }
            firstResult = pageable.offset.toInt()
            maxResults = pageable.pageSize
        }.resultList

        val countQuery = entityManager.createQuery(
            "SELECT COUNT(gathering) FROM GatheringJpaEntity gathering WHERE 1 = 1${where.clause}",
            Long::class.javaObjectType,
        ).apply {
            where.params.forEach { (key, value) -> setParameter(key, value) }
        }

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.singleResult }
    }

    private data class DynamicWhere(val clause: String, val params: Map<String, Any>)

    private fun buildWhere(filter: LoadGatheringFilter, now: LocalDateTime): DynamicWhere {
        val sb = StringBuilder()
        val params = mutableMapOf<String, Any>()

        params["publicVisible"] = GatheringVisible.PUBLIC
        if (filter.accountId != null) {
            sb.append(
                " AND (gathering.visible = :publicVisible" +
                        " OR EXISTS (SELECT 1 FROM CardJpaEntity ac" +
                        " WHERE ac.gathering = gathering AND ac.account.id = :accessAccountId))"
            )
            params["accessAccountId"] = filter.accountId
        } else {
            sb.append(" AND gathering.visible = :publicVisible")
        }

        filter.teamId?.let { sb.append(" AND gathering.team.id = :teamId"); params["teamId"] = it }
        filter.visible?.let { sb.append(" AND gathering.visible = :visible"); params["visible"] = it }

        filter.progress?.let {
            when (it) {
                PeriodStatus.UPCOMING -> sb.append(" AND gathering.startAt > :now")
                PeriodStatus.ONGOING -> sb.append(" AND gathering.startAt <= :now AND gathering.endAt >= :now")
                PeriodStatus.ENDED -> sb.append(" AND gathering.endAt < :now")
            }
            params["now"] = now
        }
        filter.registration?.let {
            when (it) {
                PeriodStatus.UPCOMING -> sb.append(" AND gathering.registerStartAt > :now")
                PeriodStatus.ONGOING -> sb.append(" AND gathering.registerStartAt <= :now AND gathering.registerEndAt >= :now")
                PeriodStatus.ENDED -> sb.append(" AND gathering.registerEndAt < :now")
            }
            params["now"] = now
        }

        if (filter.accountId != null) {
            filter.participated?.let {
                val exists =
                    "EXISTS (SELECT 1 FROM CardJpaEntity c WHERE c.gathering = gathering AND c.account.id = :accountId)"
                sb.append(if (it) " AND $exists" else " AND NOT $exists")
                params["accountId"] = filter.accountId
            }
        }

        return DynamicWhere(sb.toString(), params)
    }

    private val sortable = mapOf(
        "startAt" to "gathering.startAt",
        "registerStartAt" to "gathering.registerStartAt",
    )

    private fun orderBy(sort: Sort): String {
        val orders = sort.mapNotNull { order ->
            sortable[order.property]?.let { column -> "$column ${if (order.isAscending) "ASC" else "DESC"}" }
        }
        return if (orders.isEmpty()) "ORDER BY gathering.startAt DESC, gathering.id DESC"
        else "ORDER BY ${orders.joinToString(", ")}, gathering.id DESC"
    }
}
