package sharev.gathering.adapter.inbound.web.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpdateIntroduceTemplateRequest(
    @field:NotBlank
    val content: String?,

    @field:NotNull
    val placeholders: Map<String, String>?,
) {
}
