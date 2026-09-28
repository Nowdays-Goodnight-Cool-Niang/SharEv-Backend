package sharev.team.application.port.outbound

import java.time.LocalDateTime

fun interface TeamGatheringPort {
    fun findGatheringsByTeamId(teamId: Long): List<TeamGatherings>
}

data class TeamGatherings(
    val title: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val place: String?,
)
