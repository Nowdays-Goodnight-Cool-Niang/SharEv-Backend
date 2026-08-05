package sharev.member.application.port.outbound

fun interface CheckMemberPort {
    fun isMember(accountId: Long, teamId: Long): Boolean
}
