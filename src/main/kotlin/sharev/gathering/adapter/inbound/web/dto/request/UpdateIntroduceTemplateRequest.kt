package sharev.gathering.adapter.inbound.web.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpdateIntroduceTemplateRequest(
    @field:NotBlank
    val text: String?,

    @field:NotNull
    val fieldPlaceholders: Map<String, String>?,
) {
}
