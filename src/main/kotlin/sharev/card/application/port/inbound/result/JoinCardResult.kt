package sharev.card.application.port.inbound.result

data class JoinCardResult(
    val cardId: Long,
    val pinNumber: Int,
)
