package sharev.connection.adapter.inbound.event

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import sharev.card.domain.event.ShowCardEvent
import sharev.connection.application.port.inbound.command.ConnectCardsCommand
import sharev.connection.application.port.inbound.usecase.ConnectCardsUseCase

@Component
class ShowCardEventListener(
    private val connectCardsUseCase: ConnectCardsUseCase,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Async
    @EventListener
    fun connect(showCardEvent: ShowCardEvent) {
        try {
            connectCardsUseCase.connect(
                ConnectCardsCommand(
                    gatheringId = showCardEvent.eventId,
                    accountId = showCardEvent.accountId,
                    targetCardId = showCardEvent.targetCardId,
                )
            )
        } catch (e: Exception) {
            log.error(
                "Failed to process ShowCardEvent - eventId: {}, accountId: {}, targetCardId: {}",
                showCardEvent.eventId,
                showCardEvent.accountId,
                showCardEvent.targetCardId,
                e,
            )
        }
    }
}
