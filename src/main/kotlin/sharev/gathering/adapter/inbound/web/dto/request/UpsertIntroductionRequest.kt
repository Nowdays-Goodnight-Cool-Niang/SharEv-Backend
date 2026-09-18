package sharev.gathering.adapter.inbound.web.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpsertIntroductionRequest(
    @field:NotBlank
    val source: String?,

    @field:NotNull
    val fields: Map<String, FieldSpecRequest>?,
)
