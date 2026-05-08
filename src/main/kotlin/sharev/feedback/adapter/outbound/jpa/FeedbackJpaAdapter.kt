package sharev.feedback.adapter.outbound.jpa

import org.springframework.stereotype.Component

@Component
class FeedbackJpaAdapter(
    private val feedbackRepository: sharev.feedback.adapter.outbound.jpa.repository.FeedbackRepository
) : sharev.feedback.application.port.outbound.SaveFeedbackPort {

    override fun save(content: String) {
        feedbackRepository.save(
            _root_ide_package_.sharev.feedback.adapter.outbound.jpa.entity.FeedbackJpaEntity(
                null,
                content
            )
        )
    }
}
