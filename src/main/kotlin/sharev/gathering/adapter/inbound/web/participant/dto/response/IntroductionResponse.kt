package sharev.gathering.adapter.inbound.web.participant.dto.response

data class IntroductionResponse(
    val version: Int,
    val source: String,
    val fields: Map<String, FieldSpecResponse>,
)

data class FieldSpecResponse(
    val placeholder: String,
)
