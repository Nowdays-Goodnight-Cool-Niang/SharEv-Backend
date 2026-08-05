package sharev.team.application.port.outbound

import sharev.team.application.port.outbound.summary.GatheringSummary

fun interface QueryGatheringPort {
    fun findByTeam(teamId: Long): List<GatheringSummary>
}
