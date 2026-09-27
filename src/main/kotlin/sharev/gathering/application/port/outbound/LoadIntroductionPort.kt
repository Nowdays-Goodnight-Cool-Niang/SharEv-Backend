package sharev.gathering.application.port.outbound

import sharev.gathering.domain.model.Introduction
import java.util.*

interface LoadIntroductionPort {
    fun loadLatestIntroduction(gatheringId: UUID): Introduction
    fun loadLatestIntroductionOrNull(gatheringId: UUID): Introduction?
    fun loadByGatheringAndVersion(gatheringId: UUID, version: Int): Introduction
}
