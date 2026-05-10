package sharev.account.application.port.inbound.usecase

import sharev.account.application.port.inbound.command.DeleteAccountCommand
import sharev.account.application.port.inbound.result.DeleteAccountResult

fun interface DeleteAccountUseCase {
    fun delete(command: DeleteAccountCommand): DeleteAccountResult
}
