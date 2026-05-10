package sharev.member.application.port.inbound.usecase

import sharev.member.application.port.inbound.command.RemoveMemberCommand
import sharev.member.application.port.inbound.result.RemoveMemberResult

fun interface RemoveMemberUseCase {
    fun removeMember(command: RemoveMemberCommand): RemoveMemberResult
}
