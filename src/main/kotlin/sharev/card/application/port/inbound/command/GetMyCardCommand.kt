package sharev.card.application.port.inbound.command

import java.util.*

data class GetMyCardCommand(
    val gatheringId: UUID,
    val accountId: Long,
)
