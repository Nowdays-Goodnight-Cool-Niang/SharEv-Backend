package sharev.gathering.adapter.inbound.web.console.dto.request

import jakarta.validation.constraints.NotBlank

data class FieldSpecRequest(
    @field:NotBlank
    val placeholder: String,
)
