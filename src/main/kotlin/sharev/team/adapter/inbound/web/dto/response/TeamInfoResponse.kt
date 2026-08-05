package sharev.team.adapter.inbound.web.dto.response

import java.time.LocalDateTime

data class TeamInfoResponse(
    val id: Long,
    val title: String?,
    val content: String?,
    val createdAt: LocalDateTime?,
    val memberRole: String,
    val headcount: Int,
)
