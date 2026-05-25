package sharev.gathering.application.port.outbound

import sharev.gathering.domain.model.Gathering

fun interface LoadParticipatedGatheringsPort {
    fun loadParticipatedGatherings(accountId: Long): List<Gathering>
}
