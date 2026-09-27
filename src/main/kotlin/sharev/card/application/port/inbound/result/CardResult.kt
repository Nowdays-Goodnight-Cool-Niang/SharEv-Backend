package sharev.card.application.port.inbound.result

import sharev.card.domain.model.CardDisplay

data class CardResult(
    val type: CardDisplay,
    val cardId: Long,
    val name: String,
    val email: String,
    val linkUrls: List<String>,
    val lastIntroductionVersion: Int,
    val nowIntroductionVersion: Int,
    val introductionSource: String,
    val fieldValues: Map<String, String>,
)
