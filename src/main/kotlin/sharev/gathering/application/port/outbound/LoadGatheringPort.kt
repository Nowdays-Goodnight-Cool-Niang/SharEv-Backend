package sharev.gathering.application.port.outbound

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.gathering.domain.model.Gathering
import java.util.*

interface LoadGatheringPort {
    fun load(gatheringId: UUID): Gathering
    fun loadAll(pageable: Pageable): Page<Gathering>
    fun loadAllByTeam(teamId: Long): List<Gathering>
}
