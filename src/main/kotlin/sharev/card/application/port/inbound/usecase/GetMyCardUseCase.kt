package sharev.card.application.port.inbound.usecase

import sharev.card.application.port.inbound.command.GetMyCardCommand
import sharev.card.application.port.inbound.result.CardResult

fun interface GetMyCardUseCase {
    fun getMyCard(command: GetMyCardCommand): CardResult
}
