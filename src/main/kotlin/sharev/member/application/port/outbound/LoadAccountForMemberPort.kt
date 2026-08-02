package sharev.member.application.port.outbound

fun interface LoadAccountForMemberPort {
    fun loadAccountIdByHandle(handle: String): Long
}
