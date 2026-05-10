package sharev.card.application.port.outbound.result

import java.util.*

data class TempCard(
    val connectionFlag: Boolean,
    val cardId: Long,
    val gatheringId: UUID,
    val accountId: Long,
    val name: String,
    val email: String,
    val templateVersion: Int,
    val templateText: String,
    val introductionText: Map<String, String>,
)
