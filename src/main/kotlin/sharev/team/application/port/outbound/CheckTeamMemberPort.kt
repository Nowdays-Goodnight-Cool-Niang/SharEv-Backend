package sharev.team.application.port.outbound

interface CheckTeamMemberPort {
    fun isMember(accountId: Long, teamId: Long): Boolean
    fun isAdminMember(accountId: Long, teamId: Long): Boolean
}
