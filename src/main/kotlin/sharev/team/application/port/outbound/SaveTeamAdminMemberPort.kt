package sharev.team.application.port.outbound

fun interface SaveTeamAdminMemberPort {
    fun saveTeamAdmin(teamId: Long, accountId: Long)
}
