package sharev.card.application.port.inbound.usecase

import org.springframework.data.domain.Page
import sharev.card.application.port.inbound.command.GetAllCardsCommand
import sharev.card.application.port.inbound.result.CardResult

fun interface GetAllCardsUseCase {
    fun getAllCards(command: GetAllCardsCommand): Page<CardResult>
}
