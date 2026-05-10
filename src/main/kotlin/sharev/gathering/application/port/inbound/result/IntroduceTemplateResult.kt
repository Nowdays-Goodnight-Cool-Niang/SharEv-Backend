package sharev.gathering.application.port.inbound.result

data class IntroduceTemplateResult(
    val version: Int,
    val text: String,
    val fieldPlaceholders: Map<String, String>,
)
