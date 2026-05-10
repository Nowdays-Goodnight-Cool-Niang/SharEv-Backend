package sharev.feedback.adapter.outbound.jpa

import org.springframework.stereotype.Component
import sharev.feedback.adapter.outbound.jpa.entity.FeedbackJpaEntity
import sharev.feedback.adapter.outbound.jpa.repository.FeedbackRepository
import sharev.feedback.application.port.outbound.SaveFeedbackPort

@Component
class FeedbackJpaAdapter(
    private val feedbackRepository: FeedbackRepository
) : SaveFeedbackPort {

    override fun save(content: String) {
        feedbackRepository.save(
            FeedbackJpaEntity(
                null,
                content
            )
        )
    }
}
