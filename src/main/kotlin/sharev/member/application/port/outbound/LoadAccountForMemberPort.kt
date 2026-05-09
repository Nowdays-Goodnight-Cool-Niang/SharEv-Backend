package sharev.member.application.port.outbound

fun interface LoadAccountForMemberPort {
    fun loadAccountIdByEmail(email: String): Long
}
