package sharev.card.application.port.inbound.result

data class UpdateCardInfoResult(
    val templateVersion: Int,
    val introductionText: Map<String, String>,
)
