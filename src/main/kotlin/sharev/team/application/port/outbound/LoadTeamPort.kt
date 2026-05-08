package sharev.team.application.port.outbound

import sharev.team.domain.model.Team

interface LoadTeamPort {
    fun load(teamId: Long): Team
    fun exists(teamId: Long): Boolean
}
