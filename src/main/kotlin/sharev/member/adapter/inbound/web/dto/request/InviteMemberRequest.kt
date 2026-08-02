package sharev.member.adapter.inbound.web.dto.request

import jakarta.validation.constraints.NotBlank

data class InviteMemberRequest(
    @field:NotBlank
    val handle: String,
)
