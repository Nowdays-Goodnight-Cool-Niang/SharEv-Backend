package sharev.gathering.adapter.outbound.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import sharev.gathering.adapter.outbound.jpa.entity.IntroductionJpaEntity
import java.util.*

interface IntroductionRepository : JpaRepository<IntroductionJpaEntity, Long> {
    fun findByGatheringIdAndVersion(gatheringId: UUID, version: Int): IntroductionJpaEntity?
    fun findTopByGatheringIdOrderByIdDesc(gatheringId: UUID): IntroductionJpaEntity?
}
