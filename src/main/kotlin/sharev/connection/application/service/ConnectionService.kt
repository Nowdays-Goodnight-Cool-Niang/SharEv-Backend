package sharev.connection.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.card.application.port.outbound.LoadCardPort
import sharev.card.domain.exception.CardException
import sharev.card.domain.exception.CardExceptionCode as CardCode
import sharev.connection.domain.exception.ConnectionExceptionCode as ConnectionCode

@Service
@Transactional(readOnly = true)
class ConnectionService(
    private val loadCardPort: LoadCardPort,
    private val saveConnectionPort: sharev.connection.application.port.outbound.SaveConnectionPort,
) : sharev.connection.application.port.inbound.usecase.ConnectCardsUseCase {

    @Transactional
    override fun connect(command: sharev.connection.application.port.inbound.command.ConnectCardsCommand) {
        val card = loadCardPort.loadByGatheringAndAccount(command.gatheringId, command.accountId)
        val targetCard = loadCardPort.load(command.targetCardId)

        if (targetCard.gatheringId != command.gatheringId) {
            throw CardException(CardCode.CARD_NOT_FOUND)
        }

        if (card.id == targetCard.id) {
            throw _root_ide_package_.sharev.connection.domain.exception.ConnectionException(ConnectionCode.REGISTER_MYSELF)
        }

        saveConnectionPort.save(card.id, targetCard.id)
    }
}
