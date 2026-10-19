package sharev.member.adapter.inbound.web.console.mapper

import sharev.common.adapter.inbound.security.model.AccountPrincipal
import sharev.member.adapter.inbound.web.console.dto.request.InviteMemberRequest
import sharev.member.adapter.inbound.web.console.dto.request.UpdateMemberRoleRequest
import sharev.member.adapter.inbound.web.console.dto.response.*
import sharev.member.application.port.inbound.command.InviteMemberCommand
import sharev.member.application.port.inbound.command.UpdateMemberRoleCommand
import sharev.member.application.port.inbound.result.*

fun InviteMemberRequest.toCommand(accountPrincipal: AccountPrincipal, teamId: Long) =
    InviteMemberCommand(accountPrincipal.id, teamId, handle)

fun UpdateMemberRoleRequest.toCommand(accountPrincipal: AccountPrincipal, teamId: Long, memberId: Long) =
    UpdateMemberRoleCommand(accountPrincipal.id, teamId, memberId, role!!)

fun MemberResult.toResponse() = MemberResponse(memberId, name, email, role.name, status.name)

fun InviteMemberResult.toResponse() = InviteMemberResponse(memberId, role.name, status.name)

fun LeaveTeamResult.toResponse() = LeaveTeamResponse(memberId)

fun UpdateMemberRoleResult.toResponse() = UpdateMemberRoleResponse(memberId, role.name)

fun RemoveMemberResult.toResponse() = RemoveMemberResponse(memberId)
