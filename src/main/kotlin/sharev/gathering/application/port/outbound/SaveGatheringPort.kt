package sharev.gathering.application.port.outbound

import sharev.gathering.domain.model.Gathering
import java.util.*

interface SaveGatheringPort {
    fun save(gathering: Gathering): Gathering
    fun update(gathering: Gathering): Gathering
    fun softDelete(gatheringId: UUID)
}
