package sharev.gathering.application.port.outbound

import sharev.gathering.domain.model.GatheringVisible
import sharev.gathering.domain.model.PeriodStatus

data class LoadGatheringFilter(
    val accountId: Long?,
    val participated: Boolean?,
    val teamId: Long?,
    val visible: GatheringVisible?,
    val progress: PeriodStatus?,
    val registration: PeriodStatus?,
)
