package sharev.card.application.port.inbound.mapper

import sharev.card.application.port.inbound.result.CardResult
import sharev.card.application.port.outbound.result.TempCard
import sharev.card.domain.model.Card
import sharev.card.domain.model.CardDisplay

fun Card.toCardResult(
    linkUrls: List<String>,
    lastIntroduceTemplateVersion: Int,
    introduceTemplateVersion: Int,
    introduceTemplateContentText: String,
) = CardResult(
    type = CardDisplay.FULL,
    cardId = id,
    name = accountName,
    email = accountEmail,
    linkUrls = linkUrls,
    lastIntroduceTemplateVersion = lastIntroduceTemplateVersion,
    nowIntroduceTemplateVersion = introduceTemplateVersion,
    introduceTemplateContentText = introduceTemplateContentText,
    introductionText = introductionText ?: emptyMap(),
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
    lastIntroduceTemplateVersion = lastIntroduceTemplateVersion,
    nowIntroduceTemplateVersion = templateVersion,
    introduceTemplateContentText = templateText,
    introductionText = if (connectionFlag) introductionText else emptyMap(),
)
