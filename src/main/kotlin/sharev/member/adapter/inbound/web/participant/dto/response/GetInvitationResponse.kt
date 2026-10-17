package sharev.member.adapter.inbound.web.participant.dto.response

import sharev.member.domain.model.MemberRole
import sharev.team.domain.model.TeamCertification
import java.time.LocalDateTime

data class GetInvitationResponse(
    val teamId: Long,
    val title: String,
    val content: String?,
    val certification: TeamCertification,
    val role: MemberRole,
    val headcount: Long,
    val createdAt: LocalDateTime,
) {
}
