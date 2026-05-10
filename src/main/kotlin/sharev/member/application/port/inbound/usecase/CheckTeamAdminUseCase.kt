package sharev.member.application.port.inbound.usecase

fun interface CheckTeamAdminUseCase {
    fun isAdmin(teamId: Long, accountId: Long): Boolean
}
