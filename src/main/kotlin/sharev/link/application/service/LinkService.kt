package sharev.link.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.link.application.port.inbound.command.GetLinksCommand
import sharev.link.application.port.inbound.mapper.toLinkResult
import sharev.link.application.port.inbound.result.LinkResult
import sharev.link.application.port.inbound.usecase.GetLinksUseCase
import sharev.link.application.port.outbound.LoadLinkPort

@Service
@Transactional(readOnly = true)
class LinkService(
    private val loadLinkPort: LoadLinkPort,
) : GetLinksUseCase {

    override fun getLinks(command: GetLinksCommand): List<LinkResult> {
        return loadLinkPort.loadAllByAccountId(command.accountId)
            .map { it.toLinkResult() }
    }
}
