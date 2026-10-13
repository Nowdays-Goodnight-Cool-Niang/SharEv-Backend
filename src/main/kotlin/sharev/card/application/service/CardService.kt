package sharev.card.application.service

import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import sharev.account.application.port.outbound.LoadAccountPort
import sharev.card.application.port.inbound.command.*
import sharev.card.application.port.inbound.mapper.toCardResult
import sharev.card.application.port.inbound.result.CardResult
import sharev.card.application.port.inbound.result.JoinCardResult
import sharev.card.application.port.inbound.result.ParticipantFlagResult
import sharev.card.application.port.inbound.result.UpdateCardInfoResult
import sharev.card.application.port.inbound.usecase.*
import sharev.card.application.port.outbound.IssueJoinOrderPort
import sharev.card.application.port.outbound.LoadCardPort
import sharev.card.application.port.outbound.QueryCardPort
import sharev.card.application.port.outbound.SaveCardPort
import sharev.card.domain.event.ShowCardEvent
import sharev.card.domain.exception.CardException
import sharev.card.domain.exception.CardExceptionCode
import sharev.card.domain.model.Card
import sharev.card.domain.model.ShareCodeConverter
import sharev.common.application.port.outbound.PublishEventPort
import sharev.gathering.application.port.outbound.LoadGatheringPort
import sharev.gathering.application.port.outbound.LoadIntroductionPort
import sharev.link.application.port.outbound.LoadLinkPort
import java.util.*

@Service
@Transactional(readOnly = true)
class CardService(
    private val saveCardPort: SaveCardPort,
    private val loadCardPort: LoadCardPort,
    private val queryCardPort: QueryCardPort,
    private val loadGatheringPort: LoadGatheringPort,
    private val loadIntroductionPort: LoadIntroductionPort,
    private val loadAccountPort: LoadAccountPort,
    private val loadLinkPort: LoadLinkPort,
    private val publishEventPort: PublishEventPort,
    private val transactionTemplate: TransactionTemplate,
    private val issueJoinOrderPort: IssueJoinOrderPort,
    private val shareCodeConverter: ShareCodeConverter,
) : JoinCardUseCase,
    UpdateCardInfoUseCase,
    GetCardByShareCodeUseCase,
    GetMyCardUseCase,
    GetMyShareCodeUseCase,
    GetAllCardsUseCase,
    IsJoinedUseCase {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    override fun join(command: JoinCardCommand): JoinCardResult {
        val (gatheringId, accountId) = command

        validateJoinTarget(gatheringId, accountId)

        val joinOrder = transactionTemplate.execute { issueJoinOrderPort.issue(gatheringId) }!!

        val shareCode = shareCodeConverter.toShareCode(gatheringId, joinOrder)
        val card = transactionTemplate.execute { saveCardPort.join(gatheringId, accountId, joinOrder) }!!
        return JoinCardResult(card.id, shareCode)
    }

    private fun validateJoinTarget(gatheringId: UUID, accountId: Long) {
        loadGatheringPort.load(gatheringId)
        loadAccountPort.load(accountId)

        if (loadCardPort.existsByGatheringAndAccount(gatheringId, accountId)) {
            throw CardException(CardExceptionCode.JOIN_ALREADY)
        }
    }

    @Transactional
    override fun updateIntroduce(command: UpdateCardInfoCommand): UpdateCardInfoResult {
        val card = loadCardPort.loadByGatheringAndAccount(command.gatheringId, command.accountId)
        val introduction = loadIntroductionPort.loadByGatheringAndVersion(
            command.gatheringId, command.introductionVersion,
        )

        card.validateFieldValues(
            introduction.version,
            introduction.template.fields.keys,
            command.introductionVersion,
            command.fieldValues,
        )
        val updatedCard =
            saveCardPort.updateFieldValues(card.id, command.introductionVersion, command.fieldValues)

        return UpdateCardInfoResult(updatedCard.introductionVersion!!, updatedCard.fieldValues!!)
    }

    override fun getCardByShareCode(command: GetCardByShareCodeCommand): CardResult {
        val (gatheringId, accountId, shareCode) = command

        validateCompletedCard(gatheringId, accountId)

        val joinOrder = shareCodeConverter.toJoinOrder(gatheringId, shareCode)
        val targetCard = loadCardPort.loadByGatheringAndJoinOrder(gatheringId, joinOrder)
        publishEventPort.publish(ShowCardEvent(gatheringId, accountId, targetCard.id))
        return calculateCardResult(gatheringId, targetCard)
    }

    override fun getMyCard(command: GetMyCardCommand): CardResult {
        validateCompletedCard(command.gatheringId, command.accountId)

        val card = loadCardPort.loadByGatheringAndAccount(command.gatheringId, command.accountId)
        return calculateCardResult(command.gatheringId, card)
    }

    override fun getMyShareCode(command: GetMyShareCodeCommand): Int {
        val (gatheringId, accountId) = command

        validateCompletedCard(gatheringId, accountId)

        val joinOrder = loadCardPort.loadByGatheringAndAccount(gatheringId, accountId).joinOrder
        return shareCodeConverter.toShareCode(gatheringId, joinOrder)
    }

    private fun calculateCardResult(gatheringId: UUID, card: Card): CardResult {
        val linkUrls = loadLinkPort.loadAllByAccountId(card.accountId)
            .map { it.url }
        val latestIntroduction = loadIntroductionPort.loadLatestIntroduction(gatheringId)
        val introductionVersion = card.introductionVersion
            ?: throw CardException(CardExceptionCode.CARD_NOT_FOUND)
        val introduction = loadIntroductionPort.loadByGatheringAndVersion(gatheringId, introductionVersion)

        return card.toCardResult(
            linkUrls = linkUrls,
            lastIntroductionVersion = latestIntroduction.version,
            nowIntroductionVersion = introduction.version,
            introductionSource = introduction.template.source,
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
        val latestIntroduction = loadIntroductionPort.loadLatestIntroduction(command.gatheringId)
        val accountIds = tempCards.content.map { it.accountId }.distinct()
        val accountLinks = loadLinkPort.loadAllByAccountIdIn(accountIds)
            .groupBy { it.accountId }

        return tempCards.map { temp ->
            temp.toCardResult(
                accountLinks[temp.accountId]?.map { it.url }.orEmpty(),
                latestIntroduction.version,
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
