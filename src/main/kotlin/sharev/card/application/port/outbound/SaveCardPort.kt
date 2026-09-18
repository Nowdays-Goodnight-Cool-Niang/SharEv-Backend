package sharev.card.application.port.outbound

import sharev.card.domain.model.Card
import java.util.*

interface SaveCardPort {
    fun join(gatheringId: UUID, accountId: Long, pinNumber: Int): Card
    fun updateFieldValues(
        cardId: Long,
        introductionVersion: Int,
        fieldValues: Map<String, String>,
    ): Card
}
