package sharev.feedback.adapter.inbound.event

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import sharev.account.domain.event.AccountWithdrawalFeedbackSubmittedEvent
import sharev.feedback.application.port.inbound.command.SaveFeedbackCommand

@Component
class FeedbackEventListener(
    private val saveFeedbackUseCase: sharev.feedback.application.port.inbound.usecase.SaveFeedbackUseCase,
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun listen(event: AccountWithdrawalFeedbackSubmittedEvent) {
        saveFeedbackUseCase.save(
            SaveFeedbackCommand(
                event.feedback
            )
        )
    }
}
