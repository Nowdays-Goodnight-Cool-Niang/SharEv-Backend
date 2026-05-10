package sharev.team.application.port.inbound.result

import java.time.LocalDateTime

data class TeamInfoResult(
    val id: Long,
    val title: String,
    val content: String?,
    val createdAt: LocalDateTime?,
    val memberRole: String,
    val headcount: Int,
)
