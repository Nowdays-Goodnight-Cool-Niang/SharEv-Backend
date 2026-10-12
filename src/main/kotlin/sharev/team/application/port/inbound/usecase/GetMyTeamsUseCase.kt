package sharev.team.application.port.inbound.usecase

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.team.application.port.inbound.command.GetMyTeamsCommand
import sharev.team.application.port.inbound.result.JoinedTeamResult

fun interface GetMyTeamsUseCase {
    fun getJoinedTeams(command: GetMyTeamsCommand, pageable: Pageable): Page<JoinedTeamResult>
}
