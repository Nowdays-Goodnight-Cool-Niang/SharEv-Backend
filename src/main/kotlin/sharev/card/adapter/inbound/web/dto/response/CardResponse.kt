package sharev.card.adapter.inbound.web.dto.response

import sharev.card.domain.model.CardDisplay

data class CardResponse(
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
