package sharev.member.application.port.inbound.mapper

import sharev.member.application.port.inbound.result.*
import sharev.member.domain.model.Member

fun Member.toResult() = MemberResult(
    memberId = id,
    name = accountName,
    email = accountEmail,
    role = role,
    status = status,
)

fun Member.toInviteMemberResult() = InviteMemberResult(
    memberId = id,
    role = role,
    status = status,
)

fun Member.toAcceptInvitationResult() = AcceptInvitationResult(
    memberId = id,
    status = status,
)

fun Member.toLeaveTeamResult() = LeaveTeamResult(
    memberId = id,
)

fun Member.toUpdateMemberRoleResult() = UpdateMemberRoleResult(
    memberId = id,
    role = role,
)

fun Member.toRemoveMemberResult() = RemoveMemberResult(
    memberId = id,
)
