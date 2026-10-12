package sharev.team.application.port.inbound.result

import java.time.LocalDateTime

data class JoinedTeamResult(
    val id: Long,
    val title: String,
    val content: String?,
    val createdAt: LocalDateTime?,
    val memberRole: String,
    val headcount: Long,
)
