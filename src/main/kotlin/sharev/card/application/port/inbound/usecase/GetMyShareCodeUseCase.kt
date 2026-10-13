package sharev.card.application.port.inbound.usecase

import sharev.card.application.port.inbound.command.GetMyShareCodeCommand

fun interface GetMyShareCodeUseCase {
    fun getMyShareCode(command: GetMyShareCodeCommand): Int
}
