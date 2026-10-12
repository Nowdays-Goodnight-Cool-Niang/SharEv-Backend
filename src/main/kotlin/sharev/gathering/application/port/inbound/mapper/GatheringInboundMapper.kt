package sharev.gathering.application.port.inbound.mapper

import sharev.gathering.application.port.inbound.result.CreateGatheringResult
import sharev.gathering.application.port.inbound.result.GatheringDetailResult
import sharev.gathering.application.port.inbound.result.GatheringResult
import sharev.gathering.application.port.inbound.result.IntroductionResult
import sharev.gathering.domain.model.Gathering
import sharev.gathering.domain.model.Introduction
import sharev.team.domain.model.Team

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

fun Gathering.toDetailResult(team: Team) = GatheringDetailResult(
    id = id,
    teamId = teamId,
    teamTitle = team.title,
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

fun Gathering.toResult() = GatheringResult(
    id = id,
    teamId = teamId,
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
