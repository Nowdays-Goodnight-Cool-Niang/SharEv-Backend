package sharev.team.application.port.outbound

import sharev.team.domain.model.Team

interface SaveTeamPort {
    fun save(title: String, content: String): Team
    fun update(teamId: Long, title: String, content: String): Team
}
