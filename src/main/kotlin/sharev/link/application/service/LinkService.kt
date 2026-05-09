package sharev.link.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.link.application.port.inbound.command.CreateLinkCommand
import sharev.link.application.port.inbound.command.DeleteLinkCommand
import sharev.link.application.port.inbound.command.GetLinksCommand
import sharev.link.application.port.inbound.mapper.toCreateLinkResult
import sharev.link.application.port.inbound.mapper.toLinkResult
import sharev.link.application.port.inbound.result.CreateLinkResult
import sharev.link.application.port.inbound.result.DeleteLinkResult
import sharev.link.application.port.inbound.result.LinkResult
import sharev.link.application.port.inbound.usecase.CreateLinkUseCase
import sharev.link.application.port.inbound.usecase.DeleteLinkUseCase
import sharev.link.application.port.inbound.usecase.GetLinksUseCase
import sharev.link.application.port.outbound.DeleteLinkPort
import sharev.link.application.port.outbound.LoadLinkPort
import sharev.link.application.port.outbound.SaveLinkPort
import sharev.link.domain.exception.LinkException
import sharev.link.domain.exception.LinkExceptionCode

@Service
@Transactional(readOnly = true)
class LinkService(
    private val saveLinkPort: SaveLinkPort,
    private val loadLinkPort: LoadLinkPort,
    private val deleteLinkPort: DeleteLinkPort,
) : CreateLinkUseCase, GetLinksUseCase, DeleteLinkUseCase {

    @Transactional
    override fun create(command: CreateLinkCommand): CreateLinkResult {
        return saveLinkPort.save(command.accountId, command.url)
            .toCreateLinkResult()
    }

    override fun getLinks(command: GetLinksCommand): List<LinkResult> {
        return loadLinkPort.loadAllByAccountId(command.accountId)
            .map { it.toLinkResult() }
    }

    @Transactional
    override fun delete(command: DeleteLinkCommand): DeleteLinkResult {
        val link = loadLinkPort.load(command.linkId)

        if (link.accountId != command.accountId) {
            throw LinkException(LinkExceptionCode.LINK_OWNERSHIP_MISMATCH)
        }

        deleteLinkPort.delete(command.linkId)

        return DeleteLinkResult(command.linkId)
    }
}
