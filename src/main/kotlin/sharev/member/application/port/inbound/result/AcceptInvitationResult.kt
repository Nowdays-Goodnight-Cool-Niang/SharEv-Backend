package sharev.member.application.port.inbound.result

import sharev.member.domain.model.MemberStatus

data class AcceptInvitationResult(
    val memberId: Long,
    val status: MemberStatus,
)
