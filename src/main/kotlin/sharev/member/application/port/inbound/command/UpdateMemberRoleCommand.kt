package sharev.member.application.port.inbound.command

import sharev.member.domain.model.MemberRole

data class UpdateMemberRoleCommand(
    val accountId: Long,
    val teamId: Long,
    val memberId: Long,
    val role: MemberRole,
)
