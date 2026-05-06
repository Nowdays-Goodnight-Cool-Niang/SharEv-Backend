package sharev.feedback.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.feedback.application.port.inbound.command.SaveFeedbackCommand
import sharev.feedback.application.port.inbound.usecase.SaveFeedbackUseCase
import sharev.feedback.application.port.outbound.SaveFeedbackPort

@Service
@Transactional(readOnly = true)
class FeedbackService(
    private val saveFeedbackPort: SaveFeedbackPort
) : SaveFeedbackUseCase {

    @Transactional
    override fun save(command: SaveFeedbackCommand) {
        saveFeedbackPort.save(command.feedback)
    }
}
