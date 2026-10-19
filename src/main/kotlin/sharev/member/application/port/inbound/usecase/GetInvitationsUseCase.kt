package sharev.member.application.port.inbound.usecase

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.member.application.port.inbound.command.GetInvitationsCommand
import sharev.member.application.port.inbound.result.GetInvitationResult

fun interface GetInvitationsUseCase {
    fun getInvitations(command: GetInvitationsCommand, pageable: Pageable): Page<GetInvitationResult>
}
