package sharev.link.application.port.inbound.command

data class CreateLinkCommand(
    val accountId: Long,
    val url: String,
)
