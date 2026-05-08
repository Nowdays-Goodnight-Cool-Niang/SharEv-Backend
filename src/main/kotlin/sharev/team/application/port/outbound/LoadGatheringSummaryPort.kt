package sharev.team.application.port.outbound

import sharev.team.application.port.outbound.summery.GatheringSummary

fun interface LoadGatheringSummaryPort {
    fun loadByTeam(teamId: Long): List<GatheringSummary>
}
