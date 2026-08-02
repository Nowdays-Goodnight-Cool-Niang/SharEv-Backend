package sharev.team.application.port.outbound

import sharev.team.domain.model.Team
import sharev.team.domain.model.TeamType

interface SaveTeamPort {
    fun save(title: String?, content: String, type: TeamType): Team
    fun update(teamId: Long, title: String, content: String): Team
}
