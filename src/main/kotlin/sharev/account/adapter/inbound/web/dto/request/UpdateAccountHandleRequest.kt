package sharev.account.adapter.inbound.web.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import sharev.account.domain.model.HandleValidator

data class UpdateAccountHandleRequest(
    @field:NotBlank
    @field:Pattern(regexp = HandleValidator.REGEX_PATTERN, message = HandleValidator.REGEX_MESSAGE)
    val handle: String
)
