package sharev.member.adapter.inbound.web.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class InviteMemberRequest(
    @field:NotBlank
    @field:Email
    val email: String,
)
