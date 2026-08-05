package sharev.team.adapter.inbound.web.dto.response

import java.time.LocalDateTime

data class TeamDetailResponse(
    val id: Long,
    val title: String?,
    val content: String?,
    val createdAt: LocalDateTime?,
    val headcount: Int,
    val certification: String,
    val gatherings: List<GatheringInfoResponse>,
    val members: List<TeamMemberInfoResponse>,
)

data class GatheringInfoResponse(
    val title: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val place: String?,
)

data class TeamMemberInfoResponse(
    val name: String,
    val email: String,
    val role: String,
)
