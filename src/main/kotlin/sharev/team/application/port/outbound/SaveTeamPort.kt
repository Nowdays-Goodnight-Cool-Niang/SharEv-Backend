package sharev.team.application.port.outbound

import sharev.team.domain.model.Team

interface SaveTeamPort {
    fun save(team: Team): Team
    fun updateTitleAndContent(team: Team): Team
}
