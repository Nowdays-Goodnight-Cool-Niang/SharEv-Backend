package sharev.member.application.port.inbound.result

import sharev.member.domain.model.MemberRole
import sharev.member.domain.model.MemberStatus

data class InviteMemberResult(
    val memberId: Long,
    val role: MemberRole,
    val status: MemberStatus,
)
