package sharev.card.application.port.outbound

import sharev.card.domain.model.Card
import java.util.*

interface SaveCardPort {
    fun join(gatheringId: UUID, accountId: Long, pinNumber: Int): Card
    fun updateIntroductionText(
        cardId: Long,
        templateVersion: Int,
        introductionText: Map<String, String>,
    ): Card
}
