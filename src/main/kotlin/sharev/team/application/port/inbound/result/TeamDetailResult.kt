package sharev.team.application.port.inbound.result

import sharev.member.domain.model.MemberRole
import java.time.LocalDateTime

data class TeamDetailResult(
    val id: Long,
    val title: String,
    val content: String?,
    val createdAt: LocalDateTime?,
    val headcount: Int,
    val gatherings: List<GatheringInfoResult>,
    val members: List<TeamMemberInfoResult>,
)

data class GatheringInfoResult(
    val title: String,
    val startAt: LocalDateTime?,
    val endAt: LocalDateTime?,
    val place: String?,
)

data class TeamMemberInfoResult(
    val name: String,
    val email: String,
    val role: MemberRole,
)
