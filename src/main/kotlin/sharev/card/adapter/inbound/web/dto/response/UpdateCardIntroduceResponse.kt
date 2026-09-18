package sharev.card.adapter.inbound.web.dto.response

data class UpdateCardIntroduceResponse(
    val introductionVersion: Int,
    val fieldValues: Map<String, String>,
)
