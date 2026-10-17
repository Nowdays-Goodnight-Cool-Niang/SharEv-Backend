package sharev.member.adapter.inbound.web.participant.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sharev.common.adapter.inbound.security.model.AccountPrincipal
import sharev.member.adapter.inbound.web.participant.dto.response.AcceptInvitationResponse
import sharev.member.adapter.inbound.web.participant.dto.response.GetInvitationResponse
import sharev.member.adapter.inbound.web.participant.mapper.toResponse
import sharev.member.application.port.inbound.command.AcceptInvitationCommand
import sharev.member.application.port.inbound.command.GetInvitationsCommand
import sharev.member.application.port.inbound.usecase.AcceptInvitationUseCase
import sharev.member.application.port.inbound.usecase.GetInvitationsUseCase

@RestController
@RequestMapping("/members")
class MemberController(
    private val getInvitationsUseCase: GetInvitationsUseCase,
    private val acceptInvitationUseCase: AcceptInvitationUseCase,
) {

    @GetMapping("/invitations")
    fun getInvitations(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        pageable: Pageable,
    ): ResponseEntity<Page<GetInvitationResponse>> {
        val response = getInvitationsUseCase.getInvitations(
            GetInvitationsCommand(accountPrincipal.id),
            pageable,
        ).map { it.toResponse() }

        return ResponseEntity.ok(response)
    }

    @PatchMapping("/invitations/{teamId}")
    fun acceptInvitation(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PathVariable teamId: Long,
    ): ResponseEntity<AcceptInvitationResponse> {
        val response = acceptInvitationUseCase.acceptInvitation(
            AcceptInvitationCommand(accountPrincipal.id, teamId)
        ).toResponse()

        return ResponseEntity.ok(response)
    }
}
