package sharev.card.application.port.inbound.usecase

import sharev.card.application.port.inbound.command.UpdateCardInfoCommand
import sharev.card.application.port.inbound.result.UpdateCardInfoResult

fun interface UpdateCardInfoUseCase {
    fun updateIntroduce(command: UpdateCardInfoCommand): UpdateCardInfoResult
}
