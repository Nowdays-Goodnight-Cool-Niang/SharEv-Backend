package sharev.member.adapter.inbound.web.dto.request

import jakarta.validation.constraints.NotNull
import sharev.member.domain.model.MemberRole

data class UpdateMemberRoleRequest(
    @field:NotNull
    val role: MemberRole?,
)
