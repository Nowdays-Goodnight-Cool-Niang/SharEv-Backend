package sharev.member.application.port.inbound.command

data class AcceptInvitationCommand(
    val accountId: Long,
    val teamId: Long,
)
