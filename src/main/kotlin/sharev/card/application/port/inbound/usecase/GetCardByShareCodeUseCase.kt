package sharev.card.application.port.inbound.usecase

import sharev.card.application.port.inbound.command.GetCardByShareCodeCommand
import sharev.card.application.port.inbound.result.CardResult

fun interface GetCardByShareCodeUseCase {
    fun getCardByShareCode(command: GetCardByShareCodeCommand): CardResult
}
