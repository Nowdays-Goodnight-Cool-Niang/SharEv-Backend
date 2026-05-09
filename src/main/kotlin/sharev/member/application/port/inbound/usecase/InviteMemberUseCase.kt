package sharev.member.application.port.inbound.usecase

import sharev.member.application.port.inbound.command.InviteMemberCommand
import sharev.member.application.port.inbound.result.InviteMemberResult

fun interface InviteMemberUseCase {
    fun invite(command: InviteMemberCommand): InviteMemberResult
}
