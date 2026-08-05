package sharev.member.application.port.inbound.command

data class InviteMemberCommand(
    val accountId: Long,
    val teamId: Long,
    val handle: String,
)
