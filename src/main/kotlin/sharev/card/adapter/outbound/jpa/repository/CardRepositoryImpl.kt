package sharev.card.adapter.outbound.jpa.repository

import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import sharev.card.application.port.outbound.result.TempCard
import java.time.LocalDateTime
import java.util.*

@Repository
class CardRepositoryImpl(
    private val entityManager: EntityManager,
) : CardRepositoryCustom {

    override fun searchTempCards(
        gatheringId: UUID,
        myCardId: Long,
        snapshotTime: LocalDateTime,
        pageable: Pageable,
    ): Page<TempCard> {
        val rows = entityManager.createQuery(
            """
            select c.id, c.gathering.id, c.account.id, c.account.name, c.account.email,
                   c.templateVersion, t.content.text, c.introductionText,
                   case when conn.id is not null then true else false end
            from CardJpaEntity c
            join IntroduceTemplateJpaEntity t
                on t.gathering.id = c.gathering.id and t.version = c.templateVersion
            left join ConnectionJpaEntity conn
                on conn.myCard.id = :myCardId
                and conn.otherCard.id = c.id
                and conn.createdAt <= :snapshotTime
            where c.gathering.id = :gatheringId
              and c.id <> :myCardId
              and c.templateVersion is not null
              and c.introductionText is not null
            order by
                case when conn.id is not null then 1 else 2 end asc,
                c.createdAt desc
            """.trimIndent(),
            Array<Any>::class.java,
        )
            .setParameter("gatheringId", gatheringId)
            .setParameter("myCardId", myCardId)
            .setParameter("snapshotTime", snapshotTime)
            .setFirstResult(pageable.offset.toInt())
            .setMaxResults(pageable.pageSize)
            .resultList

        val content = rows.map { row ->
            @Suppress("UNCHECKED_CAST")
            TempCard(
                cardId = row[0] as Long,
                gatheringId = row[1] as UUID,
                accountId = row[2] as Long,
                name = row[3] as String,
                email = row[4] as String,
                templateVersion = row[5] as Int,
                templateText = row[6] as String,
                introductionText = row[7] as Map<String, String>,
                connectionFlag = row[8] as Boolean,
            )
        }

        val countQuery =
            entityManager.createQuery(
                """
                select count(c.id)
                from CardJpaEntity c
                join IntroduceTemplateJpaEntity t
                    on t.gathering.id = c.gathering.id and t.version = c.templateVersion
                where c.gathering.id = :gatheringId
                  and c.id <> :myCardId
                  and c.templateVersion is not null
                  and c.introductionText is not null
                """.trimIndent(),
                Long::class.java,
            )
                .setParameter("gatheringId", gatheringId)
                .setParameter("myCardId", myCardId)

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.singleResult }
    }
}
