package sharev.team.application.port.outbound

fun interface TeamMemberPort {
    fun findMembersByTeamId(teamId: Long): List<TeamMembers>
}

data class TeamMembers(
    val name: String,
    val email: String,
    val role: String,
)
