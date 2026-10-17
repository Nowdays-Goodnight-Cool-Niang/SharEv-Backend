package sharev.member.adapter.inbound.web.participant.mapper

import sharev.member.adapter.inbound.web.participant.dto.response.AcceptInvitationResponse
import sharev.member.application.port.inbound.result.AcceptInvitationResult

fun AcceptInvitationResult.toResponse() = AcceptInvitationResponse(memberId, status.name)
