package sharev.card.application.port.inbound.command

import java.util.*

data class JoinCardCommand(
    val gatheringId: UUID,
    val accountId: Long,
)
