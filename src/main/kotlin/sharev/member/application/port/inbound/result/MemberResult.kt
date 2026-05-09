package sharev.member.application.port.inbound.result

import sharev.member.domain.model.MemberRole
import sharev.member.domain.model.MemberStatus

data class MemberResult(
    val memberId: Long,
    val name: String,
    val email: String,
    val role: MemberRole,
    val status: MemberStatus,
)
