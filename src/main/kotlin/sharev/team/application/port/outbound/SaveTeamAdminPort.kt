package sharev.team.application.port.outbound

fun interface SaveTeamAdminPort {
    fun saveTeamAdmin(teamId: Long, accountId: Long)
}
