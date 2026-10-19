package sharev.gathering.adapter.inbound.web.console.mapper

import sharev.gathering.adapter.inbound.web.console.dto.request.CreateGatheringRequest
import sharev.gathering.adapter.inbound.web.console.dto.request.FieldSpecRequest
import sharev.gathering.adapter.inbound.web.console.dto.request.UpdateGatheringRequest
import sharev.gathering.adapter.inbound.web.console.dto.request.UpsertIntroductionRequest
import sharev.gathering.adapter.inbound.web.console.dto.response.*
import sharev.gathering.application.port.inbound.command.CreateGatheringCommand
import sharev.gathering.application.port.inbound.command.UpdateGatheringCommand
import sharev.gathering.application.port.inbound.command.UpsertIntroductionCommand
import sharev.gathering.application.port.inbound.result.CreateGatheringResult
import sharev.gathering.application.port.inbound.result.DeleteGatheringResult
import sharev.gathering.application.port.inbound.result.ManagedGatheringDetailResult
import sharev.gathering.application.port.inbound.result.UpsertIntroductionResult
import sharev.gathering.domain.model.FieldSpec
import java.util.*

fun ManagedGatheringDetailResult.toResponse() = ManagedGatheringDetailResponse(
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

fun UpsertIntroductionRequest.toCommand(accountId: Long, gatheringId: UUID) = UpsertIntroductionCommand(
    accountId,
    gatheringId,
    requireNotNull(source),
    requireNotNull(fields).mapValues { (_, field) -> field.toModel() }
)

fun FieldSpecRequest.toModel() = FieldSpec(
    placeholder,
)

fun UpsertIntroductionResult.toResponse() = UpsertIntroductionResponse(
    version,
    source,
    fields.mapValues { (_, field) -> field.toResponse() }
)

fun FieldSpec.toResponse() = UpsertFieldSpecResponse(
    placeholder,
)
