package sharev.member.application.port.inbound.command

data class RemoveMemberCommand(
    val accountId: Long,
    val teamId: Long,
    val memberId: Long,
)
