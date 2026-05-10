package sharev.card.application.port.inbound.command

import java.util.*

data class IsJoinedCommand(
    val gatheringId: UUID,
    val accountId: Long,
)
