package sharev.team.domain.model

import java.time.LocalDateTime

data class Team(
    val id: Long,
    val teamCertification: TeamCertification,
    val title: String,
    val content: String?,
    val createdAt: LocalDateTime?,
) {
    fun updateTitle(title: String): Team = copy(title = title)
}
