package sharev.card.application.port.inbound.usecase

import sharev.card.application.port.inbound.command.JoinCardCommand
import sharev.card.application.port.inbound.result.JoinCardResult

fun interface JoinCardUseCase {
    fun join(command: JoinCardCommand): JoinCardResult
}
