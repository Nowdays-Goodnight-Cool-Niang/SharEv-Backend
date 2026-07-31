package sharev.account.application.service

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.account.application.port.inbound.command.DeleteAccountCommand
import sharev.account.application.port.inbound.command.UpdateAccountHandleCommand
import sharev.account.application.port.inbound.command.UpdateAccountInfoCommand
import sharev.account.application.port.inbound.mapper.toUpdateAccountHandleResult
import sharev.account.application.port.inbound.mapper.toUpdateAccountInfoResult
import sharev.account.application.port.inbound.result.DeleteAccountResult
import sharev.account.application.port.inbound.result.UpdateAccountHandleResult
import sharev.account.application.port.inbound.result.UpdateAccountInfoResult
import sharev.account.application.port.inbound.usecase.DeleteAccountUseCase
import sharev.account.application.port.inbound.usecase.UpdateAccountHandleUseCase
import sharev.account.application.port.inbound.usecase.UpdateAccountInfoUseCase
import sharev.account.application.port.outbound.CheckHandleDuplicatedPort
import sharev.account.application.port.outbound.DeleteAccountPort
import sharev.account.application.port.outbound.UpdateAccountHandlePort
import sharev.account.application.port.outbound.UpdateAccountPort
import sharev.account.domain.event.AccountLinkUpdatedEvent
import sharev.account.domain.event.AccountWithdrawalFeedbackSubmittedEvent
import sharev.account.domain.exception.AccountException
import sharev.account.domain.exception.AccountExceptionCode
import sharev.account.domain.model.HandleValidator
import sharev.common.application.port.outbound.PublishEventPort

@Service
@Transactional(readOnly = true)
class AccountService(
    private val updateAccountPort: UpdateAccountPort,
    private val deleteAccountPort: DeleteAccountPort,
    private val publishEventPort: PublishEventPort,
    private val updateAccountHandlePort: UpdateAccountHandlePort,
    private val checkHandleDuplicatedPort: CheckHandleDuplicatedPort,
) : UpdateAccountInfoUseCase,
    DeleteAccountUseCase,
    UpdateAccountHandleUseCase {

    @Transactional
    override fun updateAccountInfo(command: UpdateAccountInfoCommand): UpdateAccountInfoResult {

        require(command.name.isNotBlank()) { "이름은 필수입니다." }

        val account = updateAccountPort.update(command.accountId, command.name, command.email)

        publishEventPort.publish(
            AccountLinkUpdatedEvent(
                accountId = command.accountId,
                addLinkUrls = command.addLinkUrls,
                deleteLinkIds = command.deleteLinkIds,
            )
        )

        return account.toUpdateAccountInfoResult()
    }

    @Transactional
    override fun delete(command: DeleteAccountCommand): DeleteAccountResult {
        deleteAccountPort.delete(command.accountId)

        if (command.feedback.isNotBlank()) {
            publishEventPort.publish(AccountWithdrawalFeedbackSubmittedEvent(command.feedback))
        }

        return DeleteAccountResult(command.accountId)
    }

    @Transactional
    override fun updateAccountHandle(command: UpdateAccountHandleCommand): UpdateAccountHandleResult {
        require(HandleValidator.isValid(command.handle)) { HandleValidator.REGEX_MESSAGE }

        if (checkHandleDuplicatedPort.isDuplicated(command.accountId, command.handle)) {
            throw AccountException(AccountExceptionCode.HANDLE_ALREADY_EXISTS)
        }

        try {
            val account = updateAccountHandlePort.update(command.accountId, command.handle)
            return account.toUpdateAccountHandleResult()
        } catch (e: DataIntegrityViolationException) {
            throw AccountException(AccountExceptionCode.HANDLE_ALREADY_EXISTS)
        }
    }
}
