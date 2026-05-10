package sharev.card.application.service

import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.account.application.port.outbound.LoadAccountPort
import sharev.card.application.port.inbound.command.*
import sharev.card.application.port.inbound.mapper.toCardResult
import sharev.card.application.port.inbound.result.CardResult
import sharev.card.application.port.inbound.result.JoinCardResult
import sharev.card.application.port.inbound.result.ParticipantFlagResult
import sharev.card.application.port.inbound.result.UpdateCardInfoResult
import sharev.card.application.port.inbound.usecase.*
import sharev.card.application.port.outbound.CardPinNumberPoolPort
import sharev.card.application.port.outbound.LoadCardPort
import sharev.card.application.port.outbound.QueryCardPort
import sharev.card.application.port.outbound.SaveCardPort
import sharev.card.domain.event.ShowCardEvent
import sharev.card.domain.exception.CardException
import sharev.card.domain.exception.CardExceptionCode
import sharev.card.domain.model.Card
import sharev.common.application.port.outbound.PublishEventPort
import sharev.gathering.application.port.outbound.LoadGatheringPort
import sharev.gathering.application.port.outbound.LoadIntroduceTemplatePort
import sharev.link.application.port.outbound.LoadLinkPort
import java.util.*

@Service
@Transactional(readOnly = true)
class CardService(
    private val saveCardPort: SaveCardPort,
    private val loadCardPort: LoadCardPort,
    private val queryCardPort: QueryCardPort,
    private val loadGatheringPort: LoadGatheringPort,
    private val loadIntroduceTemplatePort: LoadIntroduceTemplatePort,
    private val loadAccountPort: LoadAccountPort,
    private val loadLinkPort: LoadLinkPort,
    private val publishEventPort: PublishEventPort,
    private val cardPinNumberPoolPort: CardPinNumberPoolPort,
) : JoinCardUseCase,
    UpdateCardInfoUseCase,
    GetCardByPinNumberUseCase,
    GetMyCardUseCase,
    GetMyPinNumberUseCase,
    GetAllCardsUseCase,
    IsJoinedUseCase {

    @Transactional
    override fun join(command: JoinCardCommand): JoinCardResult {
        validateJoinTarget(command.gatheringId, command.accountId)

        val pinNumber = getPinNumber(command.gatheringId)

        return try {
            val card = saveCardPort.join(command.gatheringId, command.accountId, pinNumber)
            JoinCardResult(card.id, pinNumber)
        } catch (e: CardException) {
            if (e.details.code == CardExceptionCode.JOIN_ALREADY.name) {
                cardPinNumberPoolPort.restorePinNumber(command.gatheringId, pinNumber)
            }
            throw e
        }
    }

    private fun validateJoinTarget(gatheringId: UUID, accountId: Long) {
        loadGatheringPort.load(gatheringId)
        loadAccountPort.load(accountId)
    }

    private fun getPinNumber(gatheringId: UUID): Int {
        return cardPinNumberPoolPort.popAvailablePinNumber(gatheringId) {
            loadCardPort.loadUsedPinNumbers(gatheringId)
        } ?: throw CardException(CardExceptionCode.PIN_NUMBER_GENERATE)
    }

    @Transactional
    override fun updateIntroduce(command: UpdateCardInfoCommand): UpdateCardInfoResult {
        val card = loadCardPort.loadByGatheringAndAccount(command.gatheringId, command.accountId)
        val introduceTemplate = loadIntroduceTemplatePort.loadByGatheringAndVersion(
            command.gatheringId, command.templateVersion,
        )

        card.validateIntroductionText(
            introduceTemplate.version,
            introduceTemplate.content.getFields(),
            command.templateVersion,
            command.introductionText,
        )
        val updatedCard =
            saveCardPort.updateIntroductionText(card.id, command.templateVersion, command.introductionText)

        return UpdateCardInfoResult(updatedCard.templateVersion!!, updatedCard.introductionText!!)
    }

    override fun getCardByPinNumber(command: GetCardByPinNumberCommand): CardResult {
        validateCompletedCard(command.gatheringId, command.accountId)

        val targetCard = loadCardPort.loadByGatheringAndPinNumber(command.gatheringId, command.pinNumber)
        publishEventPort.publish(ShowCardEvent(command.gatheringId, command.accountId, targetCard.id))
        return calculateCardResult(command.gatheringId, targetCard)
    }

    override fun getMyCard(command: GetMyCardCommand): CardResult {
        validateCompletedCard(command.gatheringId, command.accountId)

        val card = loadCardPort.loadByGatheringAndAccount(command.gatheringId, command.accountId)
        return calculateCardResult(command.gatheringId, card)
    }

    override fun getMyPinNumber(command: GetMyPinNumberCommand): Int {
        validateCompletedCard(command.gatheringId, command.accountId)

        return loadCardPort.loadByGatheringAndAccount(command.gatheringId, command.accountId).pinNumber
            ?: throw CardException(CardExceptionCode.CARD_NOT_FOUND)
    }

    private fun calculateCardResult(gatheringId: UUID, card: Card): CardResult {
        val linkUrls = loadLinkPort.loadAllByAccountId(card.accountId)
            .map { it.url }
        val latestIntroduceTemplate = loadIntroduceTemplatePort.loadLatest(gatheringId)
        val templateVersion = card.templateVersion
            ?: throw CardException(CardExceptionCode.CARD_NOT_FOUND)
        val introduceTemplate = loadIntroduceTemplatePort.loadByGatheringAndVersion(gatheringId, templateVersion)

        return card.toCardResult(
            linkUrls = linkUrls,
            lastIntroduceTemplateVersion = latestIntroduceTemplate.version,
            introduceTemplateVersion = introduceTemplate.version,
            introduceTemplateContentText = introduceTemplate.content.text,
        )
    }

    override fun isJoined(command: IsJoinedCommand): ParticipantFlagResult {
        return ParticipantFlagResult(loadCardPort.existsByGatheringAndAccount(command.gatheringId, command.accountId))
    }

    override fun getAllCards(command: GetAllCardsCommand): Page<CardResult> {
        val myCardId = validateCompletedCard(command.gatheringId, command.accountId)

        val tempCards = queryCardPort.searchTempCards(
            command.gatheringId, myCardId, command.snapshotTime, command.pageable,
        )
        val latestIntroduceTemplate = loadIntroduceTemplatePort.loadLatest(command.gatheringId)
        val accountIds = tempCards.content.map { it.accountId }.distinct()
        val accountLinks = loadLinkPort.loadAllByAccountIdIn(accountIds)
            .groupBy { it.accountId }

        return tempCards.map { temp ->
            temp.toCardResult(
                accountLinks[temp.accountId]?.map { it.url }.orEmpty(),
                latestIntroduceTemplate.version,
            )
        }
    }

    private fun validateCompletedCard(gatheringId: UUID, accountId: Long): Long {
        val card = loadCardPort.loadByGatheringAndAccount(gatheringId, accountId)

        if (!card.isCompleted()) {
            throw CardException(CardExceptionCode.CARD_UNCOMPLETED)
        }

        return card.id
    }
}
