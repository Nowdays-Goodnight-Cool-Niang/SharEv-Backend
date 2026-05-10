package sharev.gathering.adapter.inbound.web.dto.response

data class IntroduceTemplateResponse(
    val version: Int,
    val text: String,
    val fieldPlaceholders: Map<String, String>,
)
