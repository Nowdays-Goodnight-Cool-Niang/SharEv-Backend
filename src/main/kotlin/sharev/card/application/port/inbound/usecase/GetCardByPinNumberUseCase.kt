package sharev.card.application.port.inbound.usecase

import sharev.card.application.port.inbound.command.GetCardByPinNumberCommand
import sharev.card.application.port.inbound.result.CardResult

fun interface GetCardByPinNumberUseCase {
    fun getCardByPinNumber(command: GetCardByPinNumberCommand): CardResult
}
