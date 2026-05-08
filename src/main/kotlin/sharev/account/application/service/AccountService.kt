package sharev.account.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.account.application.port.inbound.command.DeleteAccountCommand
import sharev.account.application.port.inbound.command.UpdateAccountInfoCommand
import sharev.account.application.port.inbound.mapper.toUpdateAccountInfoResult
import sharev.account.application.port.inbound.result.DeleteAccountResult
import sharev.account.application.port.inbound.result.UpdateAccountInfoResult
import sharev.account.application.port.inbound.usecase.DeleteAccountUseCase
import sharev.account.application.port.inbound.usecase.UpdateAccountInfoUseCase
import sharev.account.application.port.outbound.DeleteAccountPort
import sharev.account.application.port.outbound.LoadAccountPort
import sharev.account.application.port.outbound.SaveAccountPort
import sharev.account.domain.event.AccountWithdrawalFeedbackSubmittedEvent
import sharev.common.application.port.outbound.PublishEventPort

@Service
@Transactional(readOnly = true)
class AccountService(
    private val loadAccountPort: LoadAccountPort,
    private val deleteAccountPort: DeleteAccountPort,
    private val saveAccountPort: SaveAccountPort,
    private val publishEventPort: PublishEventPort,
) : UpdateAccountInfoUseCase, DeleteAccountUseCase {

    @Transactional
    override fun updateAccountInfo(
        command: UpdateAccountInfoCommand
    ): UpdateAccountInfoResult {
        val account = loadAccountPort.load(command.accountId)
        val updatedAccount = account.updateInfo(command.name, command.email)
        return saveAccountPort.save(updatedAccount)
            .toUpdateAccountInfoResult()
    }

    @Transactional
    override fun delete(command: DeleteAccountCommand): DeleteAccountResult {
        deleteAccountPort.delete(command.accountId)

        if (command.feedback.isNotBlank()) {
            publishEventPort.publish(AccountWithdrawalFeedbackSubmittedEvent(command.feedback))
        }

        return DeleteAccountResult(command.accountId)
    }
}
