package sharev.member.adapter.inbound.web.participant.mapper

import sharev.member.adapter.inbound.web.participant.dto.response.AcceptInvitationResponse
import sharev.member.adapter.inbound.web.participant.dto.response.GetInvitationResponse
import sharev.member.application.port.inbound.result.AcceptInvitationResult
import sharev.member.application.port.inbound.result.GetInvitationResult

fun GetInvitationResult.toResponse() = GetInvitationResponse(
    teamId,
    title,
    content,
    certification,
    role,
    headcount,
    createdAt,
)

fun AcceptInvitationResult.toResponse() = AcceptInvitationResponse(memberId, status.name)
