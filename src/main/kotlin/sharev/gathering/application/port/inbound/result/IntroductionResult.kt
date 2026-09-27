package sharev.gathering.application.port.inbound.result

import sharev.gathering.domain.model.FieldSpec

data class IntroductionResult(
    val version: Int,
    val source: String,
    val fields: Map<String, FieldSpec>,
)
