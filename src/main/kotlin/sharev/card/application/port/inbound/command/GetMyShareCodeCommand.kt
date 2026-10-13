package sharev.card.application.port.inbound.command

import java.util.*

data class GetMyShareCodeCommand(
    val gatheringId: UUID,
    val accountId: Long,
)
