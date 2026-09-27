package sharev.card.application.port.inbound.result

data class UpdateCardInfoResult(
    val introductionVersion: Int,
    val fieldValues: Map<String, String>,
)
