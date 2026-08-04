package sharev.team.domain.model

import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import java.time.LocalDateTime

data class Team(
    val id: Long,
    val title: String?,
    val content: String?,
    val certification: TeamCertification,
    val type: TeamType,
    val createdAt: LocalDateTime?,
) {
    companion object {
        fun create(title: String?, content: String?, type: TeamType): Team {
            return Team(0L, title, content, TeamCertification.NONE, type, null)
        }
    }

    fun updateInfo(title: String, content: String): Team {
        if (type != TeamType.PUBLIC) {
            throw TeamException(TeamExceptionCode.NOT_MODIFIABLE_TEAM)
        }

        return copy(title = title, content = content)
    }
}
