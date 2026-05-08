package sharev.team.application.port.inbound.command

data class UpdateTeamInfoCommand(
    val accountId: Long,
    val teamId: Long,
    val title: String,
)
