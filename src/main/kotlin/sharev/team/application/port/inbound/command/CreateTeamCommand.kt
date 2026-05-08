package sharev.team.application.port.inbound.command

data class CreateTeamCommand(
    val accountId: Long,
    val title: String,
)
