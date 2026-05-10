package sharev.link.application.port.inbound.command

data class DeleteLinkCommand(
    val accountId: Long,
    val linkId: Long,
)
