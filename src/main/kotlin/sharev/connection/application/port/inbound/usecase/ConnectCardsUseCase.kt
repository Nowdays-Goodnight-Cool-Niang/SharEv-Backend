package sharev.connection.application.port.inbound.usecase

fun interface ConnectCardsUseCase {
    fun connect(command: sharev.connection.application.port.inbound.command.ConnectCardsCommand)
}
