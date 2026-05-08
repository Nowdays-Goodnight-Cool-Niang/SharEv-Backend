package sharev.link.application.port.inbound.usecase

import sharev.link.application.port.inbound.command.DeleteLinkCommand

fun interface DeleteLinkUseCase {
    fun delete(command: DeleteLinkCommand)
}
