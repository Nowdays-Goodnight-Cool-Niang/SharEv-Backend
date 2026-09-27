package sharev.gathering.adapter.inbound.web.dto.request

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpsertIntroductionRequest(
    @field:NotBlank
    val source: String?,

    @field:Valid
    @field:NotNull
    val fields: Map<String, FieldSpecRequest>?,
)
