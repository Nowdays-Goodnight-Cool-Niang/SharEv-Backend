package sharev.gathering.application.port.inbound.mapper

import sharev.gathering.application.port.inbound.command.GetGatheringCommand
import sharev.gathering.application.port.inbound.result.CreateGatheringResult
import sharev.gathering.application.port.inbound.result.GatheringDetailResult
import sharev.gathering.application.port.inbound.result.IntroductionResult
import sharev.gathering.application.port.outbound.LoadGatheringFilter
import sharev.gathering.application.port.outbound.summary.GatheringDetailSummary
import sharev.gathering.domain.model.Gathering
import sharev.gathering.domain.model.Introduction

fun Gathering.toCreateGatheringResult() = CreateGatheringResult(
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

fun Gathering.toDetailResult() = GatheringDetailResult(
    id = id,
    teamId = teamId,
    teamTitle = null,
    ownerHandle = null,
    visible = visible,
    title = title,
    content = content,
    startAt = startAt,
    endAt = endAt,
    place = place,
    imageUrl = imageUrl,
    gatheringUrl = gatheringUrl,
    contact = contact,
    registerStartAt = registerStartAt,
    registerEndAt = registerEndAt,
)

fun GatheringDetailSummary.toDetailResult() = GatheringDetailResult(
    id = id,
    teamId = teamId,
    teamTitle = teamTitle,
    ownerHandle = ownerHandle,
    visible = visible,
    title = title,
    content = content,
    startAt = startAt,
    endAt = endAt,
    place = place,
    imageUrl = imageUrl,
    gatheringUrl = gatheringUrl,
    contact = contact,
    registerStartAt = registerStartAt,
    registerEndAt = registerEndAt,
)

fun Introduction.toResult() = IntroductionResult(
    version = version,
    source = template.source,
    fields = template.fields,
)

fun GetGatheringCommand.toFilter() = LoadGatheringFilter(
    accountId = accountId,
    participated = participated,
    teamId = teamId,
    visible = visibility,
    progress = progress,
    registration = registration,
)
