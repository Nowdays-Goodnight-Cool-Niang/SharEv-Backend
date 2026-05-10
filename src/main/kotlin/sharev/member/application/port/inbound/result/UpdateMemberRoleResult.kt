package sharev.member.application.port.inbound.result

import sharev.member.domain.model.MemberRole

data class UpdateMemberRoleResult(
    val memberId: Long,
    val role: MemberRole,
)
