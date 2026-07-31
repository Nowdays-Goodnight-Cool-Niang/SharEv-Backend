package sharev.card.adapter.outbound.jpa

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import sharev.account.adapter.outbound.jpa.repository.AccountRepository
import sharev.account.domain.exception.AccountException
import sharev.card.adapter.outbound.jpa.entity.CardJpaEntity
import sharev.card.adapter.outbound.jpa.mapper.toDomainModel
import sharev.card.adapter.outbound.jpa.repository.CardRepository
import sharev.card.application.port.outbound.LoadCardPort
import sharev.card.application.port.outbound.QueryCardPort
import sharev.card.application.port.outbound.SaveCardPort
import sharev.card.application.port.outbound.result.TempCard
import sharev.card.domain.exception.CardException
import sharev.card.domain.model.Card
import sharev.common.adapter.outbound.jpa.exception.onUniqueViolation
import sharev.gathering.adapter.outbound.jpa.mapper.toDomainModel
import sharev.gathering.adapter.outbound.jpa.repository.GatheringRepository
import sharev.gathering.application.port.outbound.CheckGatheringParticipantPort
import sharev.gathering.application.port.outbound.LoadParticipatedGatheringsPort
import sharev.gathering.domain.exception.GatheringException
import sharev.gathering.domain.model.Gathering
import java.time.LocalDateTime
import java.util.*
import sharev.account.domain.exception.AccountExceptionCode as AccountCode
import sharev.card.domain.exception.CardExceptionCode as CardCode
import sharev.gathering.domain.exception.GatheringExceptionCode as GatheringCode

@Component
class CardJpaAdapter(
    private val cardRepository: CardRepository,
    private val gatheringRepository: GatheringRepository,
    private val accountRepository: AccountRepository,
) : SaveCardPort,
    LoadCardPort,
    QueryCardPort,
    CheckGatheringParticipantPort,
    LoadParticipatedGatheringsPort {

    override fun join(gatheringId: UUID, accountId: Long, pinNumber: Int): Card {
        val gathering = gatheringRepository.findByIdOrNull(gatheringId)
            ?: throw GatheringException(GatheringCode.GATHERING_NOT_FOUND)
        val account = accountRepository.findByIdOrNull(accountId)
            ?: throw AccountException(AccountCode.ACCOUNT_NOT_FOUND)

        return onUniqueViolation({ CardException(CardCode.JOIN_ALREADY) }) {
            cardRepository.saveAndFlush(CardJpaEntity(gathering = gathering, account = account, pinNumber = pinNumber))
                .toDomainModel()
        }
    }

    override fun updateIntroductionText(
        cardId: Long,
        templateVersion: Int,
        introductionText: Map<String, String>,
    ): Card {
        val card = cardRepository.findByIdOrNull(cardId)
            ?: throw CardException(CardCode.CARD_NOT_FOUND)

        card.updateIntroductionText(templateVersion, introductionText)

        return card.toDomainModel()
    }

    override fun loadByGatheringAndAccount(gatheringId: UUID, accountId: Long): Card {
        return cardRepository.findByGatheringIdAndAccountId(gatheringId, accountId)
            ?.toDomainModel()
            ?: throw CardException(CardCode.CARD_NOT_FOUND)
    }

    override fun loadByGatheringAndPinNumber(gatheringId: UUID, pinNumber: Int): Card {
        return cardRepository.findByGatheringIdAndPinNumber(gatheringId, pinNumber)
            ?.toDomainModel()
            ?: throw CardException(CardCode.CARD_NOT_FOUND)
    }

    override fun load(cardId: Long): Card {
        return cardRepository.findByIdOrNull(cardId)
            ?.toDomainModel()
            ?: throw CardException(CardCode.CARD_NOT_FOUND)
    }

    override fun loadUsedPinNumbers(gatheringId: UUID): Set<Int> {
        return cardRepository.findPinNumbersByGatheringId(gatheringId)
    }

    override fun existsByGatheringAndAccount(gatheringId: UUID, accountId: Long): Boolean {
        return cardRepository.existsByGatheringIdAndAccountId(gatheringId, accountId)
    }

    override fun isParticipant(gatheringId: UUID, accountId: Long): Boolean {
        return existsByGatheringAndAccount(gatheringId, accountId)
    }

    override fun searchTempCards(
        gatheringId: UUID,
        myCardId: Long,
        snapshotTime: LocalDateTime,
        pageable: Pageable,
    ): Page<TempCard> {
        return cardRepository.searchTempCards(gatheringId, myCardId, snapshotTime, pageable)
    }

    override fun loadParticipatedGatherings(accountId: Long, pageable: Pageable): Page<Gathering> {
        return cardRepository.findByAccountId(accountId, pageable)
            .map { it.gathering.toDomainModel() }
    }
}
