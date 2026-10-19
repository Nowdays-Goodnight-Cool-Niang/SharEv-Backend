package sharev.member.application.port.inbound.result

import sharev.member.domain.model.MemberRole
import sharev.team.domain.model.TeamCertification
import java.time.LocalDateTime

data class GetInvitationResult(
    val teamId: Long,
    val title: String,
    val content: String?,
    val certification: TeamCertification,
    val role: MemberRole,
    val headcount: Long,
    val createdAt: LocalDateTime,
) {
}
