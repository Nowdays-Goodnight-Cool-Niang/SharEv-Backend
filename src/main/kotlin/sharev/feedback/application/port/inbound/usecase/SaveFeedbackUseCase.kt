package sharev.feedback.application.port.inbound.usecase

fun interface SaveFeedbackUseCase {
    fun save(command: sharev.feedback.application.port.inbound.command.SaveFeedbackCommand)
}
