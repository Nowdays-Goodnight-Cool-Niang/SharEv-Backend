package sharev.card.adapter.inbound.web.participant.mapper

import sharev.card.adapter.inbound.web.participant.dto.request.UpdateCardIntroduceRequest
import sharev.card.adapter.inbound.web.participant.dto.response.CardResponse
import sharev.card.adapter.inbound.web.participant.dto.response.JoinCardResponse
import sharev.card.adapter.inbound.web.participant.dto.response.ParticipantFlagResponse
import sharev.card.adapter.inbound.web.participant.dto.response.UpdateCardIntroduceResponse
import sharev.card.application.port.inbound.command.UpdateCardInfoCommand
import sharev.card.application.port.inbound.result.CardResult
import sharev.card.application.port.inbound.result.JoinCardResult
import sharev.card.application.port.inbound.result.ParticipantFlagResult
import sharev.card.application.port.inbound.result.UpdateCardInfoResult
import java.util.*

fun UpdateCardIntroduceRequest.toCommand(gatheringId: UUID, accountId: Long) = UpdateCardInfoCommand(
    gatheringId = gatheringId,
    accountId = accountId,
    introductionVersion = requireNotNull(version),
    fieldValues = requireNotNull(fieldValues),
)

fun CardResult.toResponse() = CardResponse(
    type = type,
    cardId = cardId,
    name = name,
    email = email,
    linkUrls = linkUrls,
    lastIntroductionVersion = lastIntroductionVersion,
    nowIntroductionVersion = nowIntroductionVersion,
    introductionSource = introductionSource,
    fieldValues = fieldValues,
)

fun JoinCardResult.toResponse() = JoinCardResponse(cardId, shareCode)

fun ParticipantFlagResult.toResponse() = ParticipantFlagResponse(isParticipant)

fun UpdateCardInfoResult.toResponse() = UpdateCardIntroduceResponse(introductionVersion, fieldValues)
