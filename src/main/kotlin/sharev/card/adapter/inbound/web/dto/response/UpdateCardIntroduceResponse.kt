package sharev.card.adapter.inbound.web.dto.response

data class UpdateCardIntroduceResponse(
    val templateVersion: Int,
    val introductionText: Map<String, String>,
)
