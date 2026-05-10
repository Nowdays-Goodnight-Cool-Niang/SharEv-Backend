package sharev.gathering.application.port.outbound

import sharev.gathering.domain.model.IntroduceTemplate
import java.util.*

interface LoadIntroduceTemplatePort {
    fun loadLatest(gatheringId: UUID): IntroduceTemplate
    fun loadByGatheringAndVersion(gatheringId: UUID, version: Int): IntroduceTemplate
}
