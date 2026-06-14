package sharev.card.adapter.outbound.jpa.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sharev.card.adapter.outbound.jpa.entity.CardJpaEntity
import java.util.*

interface CardRepository : JpaRepository<CardJpaEntity, Long>,
    CardRepositoryCustom {
    fun findByGatheringIdAndAccountId(gatheringId: UUID, accountId: Long): CardJpaEntity?

    fun findByGatheringIdAndPinNumber(gatheringId: UUID, pinNumber: Int): CardJpaEntity?

    fun existsByGatheringIdAndAccountId(gatheringId: UUID, accountId: Long): Boolean

    fun findByAccountId(accountId: Long, pageable: Pageable): Page<CardJpaEntity>

    @Query("select c.pinNumber from CardJpaEntity c where c.gathering.id = :gatheringId and c.pinNumber is not null")
    fun findPinNumbersByGatheringId(gatheringId: UUID): Set<Int>
}
