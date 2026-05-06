package sharev.feedback.adapter.outbound.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import sharev.feedback.adapter.outbound.jpa.entity.FeedbackJpaEntity

interface FeedbackRepository : JpaRepository<FeedbackJpaEntity, Long> {
}
