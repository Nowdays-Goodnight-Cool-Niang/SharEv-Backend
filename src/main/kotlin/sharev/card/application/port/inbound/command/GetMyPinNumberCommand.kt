package sharev.card.application.port.inbound.command

import java.util.*

data class GetMyPinNumberCommand(
    val gatheringId: UUID,
    val accountId: Long,
)
