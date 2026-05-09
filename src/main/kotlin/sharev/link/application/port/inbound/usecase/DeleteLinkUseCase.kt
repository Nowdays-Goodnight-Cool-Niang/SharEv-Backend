package sharev.link.application.port.inbound.usecase

import sharev.link.application.port.inbound.command.DeleteLinkCommand
import sharev.link.application.port.inbound.result.DeleteLinkResult

fun interface DeleteLinkUseCase {
    fun delete(command: DeleteLinkCommand): DeleteLinkResult
}
