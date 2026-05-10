package sharev.card.application.port.inbound.command

import java.util.*

data class UpdateCardInfoCommand(
    val gatheringId: UUID,
    val accountId: Long,
    val templateVersion: Int,
    val introductionText: Map<String, String>,
)
