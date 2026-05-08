package sharev.team.application.port.inbound.usecase

fun interface CheckTeamMemberUseCase {
    fun isMember(accountId: Long, teamId: Long): Boolean
}
