package sharev.gathering.application.port.outbound

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.gathering.domain.model.Gathering

fun interface LoadParticipatedGatheringsPort {
    fun loadParticipatedGatherings(accountId: Long, pageable: Pageable): Page<Gathering>
}
