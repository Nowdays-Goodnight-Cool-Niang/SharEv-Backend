package sharev.gathering.adapter.inbound.web.dto.response

data class IntroductionResponse(
    val version: Int,
    val source: String,
    val fields: Map<String, FieldSpecResponse>,
)
