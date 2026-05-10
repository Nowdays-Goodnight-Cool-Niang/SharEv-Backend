package sharev.card.application.port.inbound.usecase

import sharev.card.application.port.inbound.command.GetMyPinNumberCommand

fun interface GetMyPinNumberUseCase {
    fun getMyPinNumber(command: GetMyPinNumberCommand): Int
}
