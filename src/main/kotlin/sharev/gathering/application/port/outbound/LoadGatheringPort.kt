package sharev.gathering.application.port.outbound

import sharev.gathering.domain.model.Gathering
import java.util.*

interface LoadGatheringPort {
    fun load(gatheringId: UUID): Gathering
    fun loadAll(): List<Gathering>
    fun loadAllByTeam(teamId: Long): List<Gathering>
}
