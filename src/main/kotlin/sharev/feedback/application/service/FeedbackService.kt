package sharev.feedback.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedbackService(
    private val saveFeedbackPort: sharev.feedback.application.port.outbound.SaveFeedbackPort
) : sharev.feedback.application.port.inbound.usecase.SaveFeedbackUseCase {

    @Transactional
    override fun save(command: sharev.feedback.application.port.inbound.command.SaveFeedbackCommand) {
        saveFeedbackPort.save(command.feedback)
    }
}
