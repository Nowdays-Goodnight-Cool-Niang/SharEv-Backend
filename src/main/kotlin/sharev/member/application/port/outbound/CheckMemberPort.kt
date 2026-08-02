package sharev.member.application.port.outbound

fun interface CheckMemberPort {
    fun isMember(teamId: Long, accountId: Long): Boolean
}
