package sharev.member.adapter.inbound.web.participant.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sharev.common.adapter.inbound.security.model.AccountPrincipal
import sharev.member.adapter.inbound.web.participant.dto.response.AcceptInvitationResponse
import sharev.member.adapter.inbound.web.participant.mapper.toResponse
import sharev.member.application.port.inbound.command.AcceptInvitationCommand
import sharev.member.application.port.inbound.usecase.AcceptInvitationUseCase

@RestController
@RequestMapping("/members")
class MemberController(
    private val acceptInvitationUseCase: AcceptInvitationUseCase,
) {

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
