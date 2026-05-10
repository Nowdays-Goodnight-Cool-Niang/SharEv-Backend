package sharev.connection.application.port.inbound.usecase

import sharev.connection.application.port.inbound.command.ConnectCardsCommand

fun interface ConnectCardsUseCase {
    fun connect(command: ConnectCardsCommand)
}
