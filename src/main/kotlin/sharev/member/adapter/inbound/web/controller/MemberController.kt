package sharev.member.adapter.inbound.web.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sharev.common.adapter.inbound.security.model.AccountPrincipal
import sharev.member.adapter.inbound.web.dto.request.InviteMemberRequest
import sharev.member.adapter.inbound.web.dto.request.UpdateMemberRoleRequest
import sharev.member.adapter.inbound.web.dto.response.*
import sharev.member.adapter.inbound.web.mapper.toCommand
import sharev.member.adapter.inbound.web.mapper.toResponse
import sharev.member.application.port.inbound.command.AcceptInvitationCommand
import sharev.member.application.port.inbound.command.GetMembersCommand
import sharev.member.application.port.inbound.command.LeaveTeamCommand
import sharev.member.application.port.inbound.command.RemoveMemberCommand
import sharev.member.application.port.inbound.usecase.*

@RestController
@RequestMapping("/teams/{teamId}/members")
class MemberController(
    private val getMembersUseCase: GetMembersUseCase,
    private val inviteMemberUseCase: InviteMemberUseCase,
    private val acceptInvitationUseCase: AcceptInvitationUseCase,
    private val leaveTeamUseCase: LeaveTeamUseCase,
    private val updateMemberRoleUseCase: UpdateMemberRoleUseCase,
    private val removeMemberUseCase: RemoveMemberUseCase,
) {

    @GetMapping
    fun getMembers(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PathVariable teamId: Long,
    ): ResponseEntity<List<MemberResponse>> {
        val response = getMembersUseCase.getMembers(
            GetMembersCommand(accountPrincipal.id, teamId)
        ).map { it.toResponse() }

        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun invite(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PathVariable teamId: Long,
        @Valid @RequestBody request: InviteMemberRequest,
    ): ResponseEntity<InviteMemberResponse> {
        val response = inviteMemberUseCase.invite(request.toCommand(accountPrincipal, teamId))
            .toResponse()

        return ResponseEntity.ok(response)
    }

    @PatchMapping("/me/accept")
    fun acceptInvitation(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PathVariable teamId: Long,
    ): ResponseEntity<AcceptInvitationResponse> {
        val response = acceptInvitationUseCase.acceptInvitation(
            AcceptInvitationCommand(accountPrincipal.id, teamId)
        ).toResponse()

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/me")
    fun leave(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PathVariable teamId: Long,
    ): ResponseEntity<LeaveTeamResponse> {
        val response = leaveTeamUseCase.leave(
            LeaveTeamCommand(accountPrincipal.id, teamId)
        ).toResponse()

        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{memberId}/role")
    fun updateRole(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PathVariable teamId: Long,
        @PathVariable memberId: Long,
        @Valid @RequestBody request: UpdateMemberRoleRequest,
    ): ResponseEntity<UpdateMemberRoleResponse> {
        val response = updateMemberRoleUseCase.updateRole(
            request.toCommand(accountPrincipal, teamId, memberId)
        ).toResponse()

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{memberId}")
    fun removeMember(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PathVariable teamId: Long,
        @PathVariable memberId: Long,
    ): ResponseEntity<RemoveMemberResponse> {
        val response = removeMemberUseCase.removeMember(
            RemoveMemberCommand(accountPrincipal.id, teamId, memberId)
        ).toResponse()

        return ResponseEntity.ok(response)
    }
}
