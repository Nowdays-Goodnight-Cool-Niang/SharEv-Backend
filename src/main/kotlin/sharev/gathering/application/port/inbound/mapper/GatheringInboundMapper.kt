package sharev.gathering.application.port.inbound.mapper

import sharev.gathering.application.port.inbound.result.CreateGatheringResult
import sharev.gathering.application.port.inbound.result.GatheringDetailResult
import sharev.gathering.application.port.inbound.result.IntroduceTemplateResult
import sharev.gathering.domain.model.Gathering
import sharev.gathering.domain.model.IntroduceTemplate

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

fun IntroduceTemplate.toResult() = IntroduceTemplateResult(
    version = version,
    text = content.text,
    fieldPlaceholders = content.fieldPlaceholders,
)
