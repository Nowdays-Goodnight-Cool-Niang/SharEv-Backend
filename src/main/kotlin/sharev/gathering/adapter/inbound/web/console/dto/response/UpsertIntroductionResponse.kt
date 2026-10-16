package sharev.gathering.adapter.inbound.web.console.dto.response

data class UpsertIntroductionResponse(
    val version: Int,
    val source: String,
    val fields: Map<String, UpsertFieldSpecResponse>,
)

data class UpsertFieldSpecResponse(
    val placeholder: String,
)
