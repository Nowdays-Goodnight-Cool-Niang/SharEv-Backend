package sharev.link.application.port.inbound.usecase

import sharev.link.application.port.inbound.command.CreateLinkCommand
import sharev.link.application.port.inbound.result.CreateLinkResult

fun interface CreateLinkUseCase {
    fun create(command: CreateLinkCommand): CreateLinkResult
}
