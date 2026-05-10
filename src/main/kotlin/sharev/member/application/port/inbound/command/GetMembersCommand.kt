package sharev.member.application.port.inbound.command

data class GetMembersCommand(
    val accountId: Long,
    val teamId: Long,
)
