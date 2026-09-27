package sharev.card.application.port.outbound.result

import java.util.*

data class TempCard(
    val connectionFlag: Boolean,
    val cardId: Long,
    val gatheringId: UUID,
    val accountId: Long,
    val name: String,
    val email: String,
    val introductionVersion: Int,
    val introductionSource: String,
    val fieldValues: Map<String, String>,
)
