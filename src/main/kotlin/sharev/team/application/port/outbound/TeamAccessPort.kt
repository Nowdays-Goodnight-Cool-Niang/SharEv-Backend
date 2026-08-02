package sharev.team.application.port.outbound

interface TeamAccessPort {
    fun hasAccess(accountId: Long, teamId: Long): Boolean
    fun canManage(accountId: Long, teamId: Long): Boolean
}
