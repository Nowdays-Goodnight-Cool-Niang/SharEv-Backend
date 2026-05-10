package sharev.member.application.port.inbound.usecase

import sharev.member.application.port.inbound.command.AcceptInvitationCommand
import sharev.member.application.port.inbound.result.AcceptInvitationResult

fun interface AcceptInvitationUseCase {
    fun acceptInvitation(command: AcceptInvitationCommand): AcceptInvitationResult
}
