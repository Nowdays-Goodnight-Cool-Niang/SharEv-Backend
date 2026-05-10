package sharev.feedback.application.port.inbound.usecase

import sharev.feedback.application.port.inbound.command.SaveFeedbackCommand

fun interface SaveFeedbackUseCase {
    fun save(command: SaveFeedbackCommand)
}
