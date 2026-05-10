package sharev.feedback.adapter.outbound.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackRepository : JpaRepository<sharev.feedback.adapter.outbound.jpa.entity.FeedbackJpaEntity, Long> {
}
