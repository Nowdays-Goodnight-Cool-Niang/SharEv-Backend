package sharev.gathering.application.port.inbound.mapper

import sharev.gathering.adapter.inbound.web.dto.request.UpdateIntroduceTemplateRequest
import sharev.gathering.application.port.inbound.command.GetGatheringCommand
import sharev.gathering.application.port.inbound.command.UpdateIntroduceTemplateCommand
import sharev.gathering.application.port.inbound.result.CreateGatheringResult
import sharev.gathering.application.port.inbound.result.GatheringDetailResult
import sharev.gathering.application.port.inbound.result.IntroduceTemplateResult
import sharev.gathering.application.port.outbound.LoadGatheringFilter
import sharev.gathering.application.port.outbound.summary.GatheringDetailSummary
import sharev.gathering.domain.model.Gathering
import sharev.gathering.domain.model.GatheringVisible
import sharev.gathering.domain.model.IntroduceTemplate
import java.util.*

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

fun IntroduceTemplate.toResult() = IntroduceTemplateResult(
    version = version,
    text = content,
    placeholders = placeholders,
)

fun GetGatheringCommand.toFilter(): LoadGatheringFilter {
    val authenticated = accountId != null

    return LoadGatheringFilter(
        accountId = accountId,
        participated = calculateParticipated(authenticated, visibility, participated),
        teamId = teamId,
        visible = if (authenticated) visibility else GatheringVisible.PUBLIC,
        progress = progress,
        registration = registration,
    )
}

fun calculateParticipated(authenticated: Boolean, visibility: GatheringVisible?, participated: Boolean?): Boolean? {
    if (!authenticated) {
        return false
    }

    if (visibility == GatheringVisible.PRIVATE) {
        return true
    }

    return participated
}

fun UpdateIntroduceTemplateRequest.toCommand(gatheringId: UUID) = UpdateIntroduceTemplateCommand(
    gatheringId,
    requireNotNull(content),
    requireNotNull(placeholders),
)
