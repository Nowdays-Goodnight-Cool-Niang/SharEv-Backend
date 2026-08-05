package sharev.team.application.port.outbound

fun interface SaveTeamAdminPort {
    fun save(teamId: Long, accountId: Long)
}
