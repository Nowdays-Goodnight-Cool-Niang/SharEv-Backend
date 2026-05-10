package sharev.link.application.port.inbound.usecase

import sharev.link.application.port.inbound.command.GetLinksCommand
import sharev.link.application.port.inbound.result.LinkResult

fun interface GetLinksUseCase {
    fun getLinks(command: GetLinksCommand): List<LinkResult>
}
