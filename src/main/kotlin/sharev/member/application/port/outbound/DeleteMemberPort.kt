package sharev.member.application.port.outbound

fun interface DeleteMemberPort {
    fun delete(memberId: Long)
}
