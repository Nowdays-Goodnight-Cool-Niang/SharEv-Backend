package sharev.account.application.port.inbound.usecase

import sharev.account.application.port.inbound.command.OAuthLoginCommand
import sharev.account.application.port.inbound.result.OAuthLoginResult

fun interface OAuthLoginUseCase {
    fun login(command: OAuthLoginCommand): OAuthLoginResult
}
