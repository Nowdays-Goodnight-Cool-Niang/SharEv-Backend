package sharev.link.adapter.inbound.web.mapper

import sharev.common.adapter.inbound.security.model.AccountPrincipal
import sharev.link.adapter.inbound.web.dto.request.CreateLinkRequest
import sharev.link.adapter.inbound.web.dto.response.CreateLinkResponse
import sharev.link.adapter.inbound.web.dto.response.LinkResponse
import sharev.link.application.port.inbound.command.CreateLinkCommand
import sharev.link.application.port.inbound.command.DeleteLinkCommand
import sharev.link.application.port.inbound.command.GetLinksCommand
import sharev.link.application.port.inbound.result.CreateLinkResult
import sharev.link.application.port.inbound.result.LinkResult

fun CreateLinkRequest.toCommand(accountPrincipal: AccountPrincipal) =
    CreateLinkCommand(accountPrincipal.id, url)

fun AccountPrincipal.toGetLinksCommand() = GetLinksCommand(id)

fun AccountPrincipal.toDeleteLinkCommand(linkId: Long) = DeleteLinkCommand(id, linkId)

fun CreateLinkResult.toResponse() = CreateLinkResponse(id, url)

fun LinkResult.toResponse() = LinkResponse(id, url)
