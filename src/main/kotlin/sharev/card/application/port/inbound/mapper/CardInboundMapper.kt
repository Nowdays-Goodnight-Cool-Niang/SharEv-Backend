package sharev.card.application.port.inbound.mapper

import sharev.card.application.port.inbound.result.CardResult
import sharev.card.application.port.outbound.result.TempCard
import sharev.card.domain.model.Card
import sharev.card.domain.model.CardDisplay

fun Card.toCardResult(
    linkUrls: List<String>,
    lastIntroductionVersion: Int,
    nowIntroductionVersion: Int,
    introductionSource: String,
) = CardResult(
    type = CardDisplay.FULL,
    cardId = id,
    name = accountName,
    email = accountEmail,
    linkUrls = linkUrls,
    lastIntroductionVersion = lastIntroductionVersion,
    nowIntroductionVersion = nowIntroductionVersion,
    introductionSource = introductionSource,
    fieldValues = fieldValues ?: emptyMap(),
)

fun TempCard.toCardResult(
    linkUrls: List<String>,
    lastIntroduceTemplateVersion: Int,
) = CardResult(
    type = if (connectionFlag) CardDisplay.FULL else CardDisplay.MINIMUM,
    cardId = cardId,
    name = name,
    email = if (connectionFlag) email else "",
    linkUrls = if (connectionFlag) linkUrls else emptyList(),
    lastIntroductionVersion = lastIntroduceTemplateVersion,
    nowIntroductionVersion = introductionVersion,
    introductionSource = introductionSource,
    fieldValues = if (connectionFlag) fieldValues else emptyMap(),
)
