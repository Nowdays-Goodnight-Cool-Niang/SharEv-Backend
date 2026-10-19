package sharev.card.adapter.inbound.web.participant.dto.response

data class UpdateCardIntroduceResponse(
    val introductionVersion: Int,
    val fieldValues: Map<String, String>,
)
