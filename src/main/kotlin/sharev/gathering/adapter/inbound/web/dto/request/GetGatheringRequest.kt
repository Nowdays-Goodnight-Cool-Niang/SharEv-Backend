package sharev.gathering.adapter.inbound.web.dto.request

import sharev.gathering.domain.model.GatheringVisible
import sharev.gathering.domain.model.PeriodStatus

data class GetGatheringRequest(
    val participated: Boolean?,
    val teamId: Long?,
    val visibility: GatheringVisible?,
    val progress: PeriodStatus?,
    val registration: PeriodStatus?,
) {
}
