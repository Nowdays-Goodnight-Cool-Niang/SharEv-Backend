package sharev.team.application.port.inbound.usecase

import sharev.team.application.port.inbound.command.CreateTeamCommand
import sharev.team.application.port.inbound.result.CreateTeamResult

fun interface CreateTeamUseCase {
    fun create(command: CreateTeamCommand): CreateTeamResult
}
