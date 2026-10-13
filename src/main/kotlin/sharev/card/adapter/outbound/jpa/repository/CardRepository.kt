package sharev.card.adapter.outbound.jpa.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import sharev.card.adapter.outbound.jpa.entity.CardJpaEntity
import java.util.*

interface CardRepository : JpaRepository<CardJpaEntity, Long>,
    CardRepositoryCustom {
    fun findByGatheringIdAndAccountId(gatheringId: UUID, accountId: Long): CardJpaEntity?

    fun findByGatheringIdAndJoinOrder(gatheringId: UUID, joinOrder: Int): CardJpaEntity?

    fun existsByGatheringIdAndAccountId(gatheringId: UUID, accountId: Long): Boolean

    fun findByAccountId(accountId: Long, pageable: Pageable): Page<CardJpaEntity>
}
