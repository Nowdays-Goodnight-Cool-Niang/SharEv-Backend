package sharev.member.application.port.inbound.command

data class LeaveTeamCommand(
    val accountId: Long,
    val teamId: Long,
)
