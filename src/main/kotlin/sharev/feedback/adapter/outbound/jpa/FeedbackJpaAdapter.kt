package sharev.feedback.adapter.outbound.jpa

import org.springframework.stereotype.Component
import sharev.feedback.adapter.outbound.jpa.entity.FeedbackJpaEntity

@Component
class FeedbackJpaAdapter(
    private val feedbackRepository: sharev.feedback.adapter.outbound.jpa.repository.FeedbackRepository
) : sharev.feedback.application.port.outbound.SaveFeedbackPort {

    override fun save(content: String) {
        feedbackRepository.save(
            FeedbackJpaEntity(
                null,
                content
            )
        )
    }
}
