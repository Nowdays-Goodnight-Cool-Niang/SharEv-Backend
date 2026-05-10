package sharev.card.application.port.inbound.command

import java.util.*

data class GetCardByPinNumberCommand(
    val gatheringId: UUID,
    val accountId: Long,
    val pinNumber: Int,
)
