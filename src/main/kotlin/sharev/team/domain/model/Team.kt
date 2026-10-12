package sharev.team.domain.model

import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import java.time.LocalDateTime

data class Team(
    val id: Long,
    val title: String,
    val content: String?,
    val certification: TeamCertification,
    val createdAt: LocalDateTime?,
) {
    init {
        if (title.isBlank()) {
            throw TeamException(TeamExceptionCode.TEAM_INFO_REQUIRED)
        }
    }

    companion object {
        fun create(title: String, content: String?): Team {
            return Team(0L, title, content, TeamCertification.NONE, null)
        }
    }

    fun updateInfo(title: String, content: String?): Team {
        return copy(title = title, content = content)
    }
}
