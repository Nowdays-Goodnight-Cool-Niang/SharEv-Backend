package sharev.team.application.port.inbound.command

import sharev.team.domain.exception.TeamException
import sharev.team.domain.exception.TeamExceptionCode
import sharev.team.domain.model.TeamType

data class CreateTeamCommand(
    val accountId: Long,
    val title: String?,
    val content: String?,
    val type: TeamType,
) {
    init {
        if (type == TeamType.PUBLIC && (title.isNullOrBlank() || content.isNullOrBlank())) {
            throw TeamException(TeamExceptionCode.TEAM_INFO_REQUIRED)
        }
    }
}
