package sharev.team.application.port.outbound

import sharev.team.domain.model.Team

interface SaveTeamPort {
    fun save(title: String): Team
    fun updateTitle(teamId: Long, title: String): Team
}
