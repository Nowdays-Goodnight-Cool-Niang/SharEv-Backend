package sharev.gathering.adapter.outbound.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import sharev.gathering.adapter.outbound.jpa.entity.IntroduceTemplateJpaEntity
import java.util.*

interface IntroduceTemplateRepository : JpaRepository<IntroduceTemplateJpaEntity, Long> {
    fun findByGatheringIdAndVersion(gatheringId: UUID, version: Int): Optional<IntroduceTemplateJpaEntity>

    fun findTopByGatheringIdOrderByVersionDesc(gatheringId: UUID): Optional<IntroduceTemplateJpaEntity>
}
