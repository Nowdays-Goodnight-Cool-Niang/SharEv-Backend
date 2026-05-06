package sharev.account.application.port.inbound.usecase

import sharev.account.application.port.inbound.command.UpdateAccountInfoCommand
import sharev.account.application.port.inbound.result.UpdateAccountInfoResult

fun interface UpdateAccountInfoUseCase {
    fun updateAccountInfo(command: UpdateAccountInfoCommand): UpdateAccountInfoResult
}
