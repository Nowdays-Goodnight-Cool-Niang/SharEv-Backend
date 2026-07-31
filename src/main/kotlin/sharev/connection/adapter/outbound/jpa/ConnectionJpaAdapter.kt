package sharev.connection.adapter.outbound.jpa

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import sharev.card.adapter.outbound.jpa.repository.CardRepository
import sharev.card.domain.exception.CardException
import sharev.common.adapter.outbound.jpa.exception.onUniqueViolation
import sharev.connection.adapter.outbound.jpa.entity.ConnectionJpaEntity
import sharev.connection.adapter.outbound.jpa.repository.ConnectionRepository
import sharev.connection.domain.exception.ConnectionException
import sharev.card.domain.exception.CardExceptionCode as CardCode
import sharev.connection.domain.exception.ConnectionExceptionCode as ConnectionCode

@Component
class ConnectionJpaAdapter(
    private val connectionRepository: ConnectionRepository,
    private val cardRepository: CardRepository,
) : sharev.connection.application.port.outbound.SaveConnectionPort {

    override fun save(myCardId: Long, otherCardId: Long) {
        val myCard = cardRepository.findByIdOrNull(myCardId)
            ?: throw CardException(CardCode.CARD_NOT_FOUND)
        val otherCard = cardRepository.findByIdOrNull(otherCardId)
            ?: throw CardException(CardCode.CARD_NOT_FOUND)

        onUniqueViolation({ ConnectionException(ConnectionCode.REGISTER_ALREADY) }) {
            connectionRepository.saveAllAndFlush(
                ConnectionJpaEntity.connect(
                    myCard,
                    otherCard
                )
            )
        }
    }
}
