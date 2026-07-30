package sharev.account.application.port.inbound.usecase

import sharev.account.application.port.inbound.command.UpdateAccountHandleCommand
import sharev.account.application.port.inbound.result.UpdateAccountHandleResult

fun interface UpdateAccountHandleUseCase {
    fun updateAccountHandle(command: UpdateAccountHandleCommand): UpdateAccountHandleResult
}
