package sharev.member.application.port.inbound.usecase

import sharev.member.application.port.inbound.command.LeaveTeamCommand
import sharev.member.application.port.inbound.result.LeaveTeamResult

fun interface LeaveTeamUseCase {
    fun leave(command: LeaveTeamCommand): LeaveTeamResult
}
