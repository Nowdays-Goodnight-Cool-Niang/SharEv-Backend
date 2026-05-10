package sharev.connection.application.port.inbound.command

import java.util.*

data class ConnectCardsCommand(
    val gatheringId: UUID,
    val accountId: Long,
    val targetCardId: Long,
)
