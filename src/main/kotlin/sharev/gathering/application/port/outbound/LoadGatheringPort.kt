package sharev.gathering.application.port.outbound

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.gathering.application.port.outbound.summary.GatheringDetailSummary
import sharev.gathering.domain.model.Gathering
import java.util.*

interface LoadGatheringPort {
    fun load(gatheringId: UUID): Gathering
    fun loadAll(filter: LoadGatheringFilter, pageable: Pageable): Page<GatheringDetailSummary>
    fun loadAllByTeam(teamId: Long): List<Gathering>
}
