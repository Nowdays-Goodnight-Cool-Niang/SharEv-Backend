package sharev.account.adapter.inbound.web.mapper

import sharev.account.adapter.inbound.web.dto.request.UpdateAccountHandleRequest
import sharev.account.adapter.inbound.web.dto.request.UpdateAccountInfoRequest
import sharev.account.adapter.inbound.web.dto.response.AccountInfoResponse
import sharev.account.adapter.inbound.web.dto.response.DeleteAccountResponse
import sharev.account.adapter.inbound.web.dto.response.UpdateAccountHandleResponse
import sharev.account.adapter.inbound.web.dto.response.UpdateAccountInfoResponse
import sharev.account.application.port.inbound.command.DeleteAccountCommand
import sharev.account.application.port.inbound.command.UpdateAccountHandleCommand
import sharev.account.application.port.inbound.command.UpdateAccountInfoCommand
import sharev.account.application.port.inbound.result.DeleteAccountResult
import sharev.account.application.port.inbound.result.UpdateAccountHandleResult
import sharev.account.application.port.inbound.result.UpdateAccountInfoResult
import sharev.common.adapter.inbound.security.model.AccountPrincipal

fun UpdateAccountInfoRequest.toCommand(accountId: Long) =
    UpdateAccountInfoCommand(
        accountId,
        name,
        email,
        requireNotNull(addLinkUrls),
        requireNotNull(deleteLinkIds)
    )

fun UpdateAccountHandleRequest.toCommand(accountId: Long) = UpdateAccountHandleCommand(accountId, handle)

fun UpdateAccountInfoResult.toUpdateAccountInfoResponse() = UpdateAccountInfoResponse(id, name, email)

fun DeleteAccountResult.toDeleteAccountResponse() = DeleteAccountResponse(id)

fun AccountPrincipal.toAccountInfoResponse() = AccountInfoResponse(id, accountName, email)

fun AccountPrincipal.toDeleteAccountCommand(feedback: String) = DeleteAccountCommand(id, feedback)

fun AccountPrincipal.updateFrom(result: UpdateAccountInfoResult) = copy(email = result.email, accountName = result.name)

fun AccountPrincipal.updateFrom(result: UpdateAccountHandleResult) = copy(handle = result.handle)

fun UpdateAccountHandleResult.toUpdateAccountHandleResponse() = UpdateAccountHandleResponse(handle)
