package sharev.gathering.adapter.inbound.web.mapper

import sharev.gathering.adapter.inbound.web.dto.request.CreateGatheringRequest
import sharev.gathering.adapter.inbound.web.dto.request.UpdateGatheringRequest
import sharev.gathering.adapter.inbound.web.dto.response.*
import sharev.gathering.application.port.inbound.command.CreateGatheringCommand
import sharev.gathering.application.port.inbound.command.UpdateGatheringCommand
import sharev.gathering.application.port.inbound.result.*
import java.util.*

fun ParticipantResult.toResponse() = ParticipantResponse(isParticipant)

fun CreateGatheringRequest.toCommand(accountId: Long, teamId: Long) = CreateGatheringCommand(
    accountId = accountId,
    teamId = teamId,
    visible = requireNotNull(visible),
    title = requireNotNull(title),
    content = requireNotNull(content),
    startAt = requireNotNull(startAt),
    endAt = requireNotNull(endAt),
    place = requireNotNull(place),
    imageUrl = imageUrl,
    gatheringUrl = gatheringUrl,
    contact = contact,
    registerStartAt = requireNotNull(registerStartAt),
    registerEndAt = requireNotNull(registerEndAt),
)

fun UpdateGatheringRequest.toCommand(accountId: Long, teamId: Long, gatheringId: UUID) = UpdateGatheringCommand(
    accountId = accountId,
    teamId = teamId,
    gatheringId = gatheringId,
    visible = requireNotNull(visible),
    title = requireNotNull(title),
    content = requireNotNull(content),
    startAt = requireNotNull(startAt),
    endAt = requireNotNull(endAt),
    place = requireNotNull(place),
    imageUrl = imageUrl,
    gatheringUrl = gatheringUrl,
    contact = contact,
    registerStartAt = requireNotNull(registerStartAt),
    registerEndAt = requireNotNull(registerEndAt),
)

fun CreateGatheringResult.toResponse() = CreateGatheringResponse(
    id,
    teamId,
    visible.name,
    title,
    content,
    startAt,
    endAt,
    place,
    imageUrl,
    gatheringUrl,
    contact,
    registerStartAt,
    registerEndAt,
)

fun DeleteGatheringResult.toResponse() = DeleteGatheringResponse(gatheringId)

fun GatheringDetailResult.toResponse() = GatheringDetailResponse(
    id,
    visible,
    title,
    content,
    startAt,
    endAt,
    place,
    imageUrl,
    gatheringUrl,
    contact,
    registerStartAt,
    registerEndAt,
)

fun IntroduceTemplateResult.toResponse() = IntroduceTemplateResponse(version, text, fieldPlaceholders)
