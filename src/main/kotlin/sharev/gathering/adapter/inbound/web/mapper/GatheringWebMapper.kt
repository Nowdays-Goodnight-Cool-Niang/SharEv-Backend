package sharev.gathering.adapter.inbound.web.mapper

import sharev.gathering.adapter.inbound.web.dto.request.*
import sharev.gathering.adapter.inbound.web.dto.response.*
import sharev.gathering.application.port.inbound.command.CreateGatheringCommand
import sharev.gathering.application.port.inbound.command.GetGatheringCommand
import sharev.gathering.application.port.inbound.command.UpdateGatheringCommand
import sharev.gathering.application.port.inbound.command.UpsertIntroductionCommand
import sharev.gathering.application.port.inbound.result.*
import sharev.gathering.domain.model.FieldSpec
import java.util.*

fun ParticipantResult.toResponse() = ParticipantResponse(isParticipant)

fun CreateGatheringRequest.toCommand(accountId: Long) = CreateGatheringCommand(
    accountId = accountId,
    teamId = requireNotNull(teamId),
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

fun UpdateGatheringRequest.toCommand(accountId: Long, gatheringId: UUID) = UpdateGatheringCommand(
    accountId = accountId,
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
    teamId,
    teamTitle,
    ownerHandle,
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

fun UpsertIntroductionRequest.toCommand(accountId: Long, gatheringId: UUID) = UpsertIntroductionCommand(
    accountId,
    gatheringId,
    requireNotNull(source),
    requireNotNull(fields).mapValues { (_, field) -> field.toModel() }
)

fun FieldSpecRequest.toModel() = FieldSpec(
    placeholder,
)
