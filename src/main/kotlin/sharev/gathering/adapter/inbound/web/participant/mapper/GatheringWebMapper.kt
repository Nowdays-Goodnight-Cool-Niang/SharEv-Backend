package sharev.gathering.adapter.inbound.web.participant.mapper

import sharev.gathering.adapter.inbound.web.participant.dto.request.GetGatheringRequest
import sharev.gathering.adapter.inbound.web.participant.dto.response.*
import sharev.gathering.application.port.inbound.command.GetGatheringCommand
import sharev.gathering.application.port.inbound.result.GatheringDetailResult
import sharev.gathering.application.port.inbound.result.GatheringResult
import sharev.gathering.application.port.inbound.result.IntroductionResult
import sharev.gathering.application.port.inbound.result.ParticipantResult
import sharev.gathering.domain.model.FieldSpec

fun ParticipantResult.toResponse() = ParticipantResponse(isParticipant)

fun GatheringDetailResult.toResponse() = GatheringDetailResponse(
    id,
    teamId,
    teamTitle,
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

fun IntroductionResult.toResponse() = IntroductionResponse(
    version,
    source,
    fields.mapValues { (_, field) -> field.toResponse() }
)

fun FieldSpec.toResponse() = FieldSpecResponse(
    placeholder,
)

fun GetGatheringRequest.toCommand(accountId: Long?) = GetGatheringCommand(
    accountId,
    participated,
    teamId,
    visibility,
    progress,
    registration,
)

fun GatheringResult.toResponse() = GatheringResponse(
    id,
    teamId,
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
