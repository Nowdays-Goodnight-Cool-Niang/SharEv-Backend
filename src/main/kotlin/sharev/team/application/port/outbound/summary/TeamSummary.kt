package sharev.team.application.port.outbound.summary

import java.time.LocalDateTime

data class TeamSummary(
    val id: Long,
    val title: String?,
    val content: String?,
    val createdAt: LocalDateTime?,
    val memberRole: String,
    val headcount: Int,
)
