package sharev.card.application.port.inbound.command

import java.util.*

data class GetCardByShareCodeCommand(
    val gatheringId: UUID,
    val accountId: Long,
    val shareCode: Int,
)
