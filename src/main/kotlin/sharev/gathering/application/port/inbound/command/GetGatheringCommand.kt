package sharev.gathering.application.port.inbound.command

import sharev.gathering.domain.model.GatheringVisible
import sharev.gathering.domain.model.PeriodStatus

data class GetGatheringCommand(
    val accountId: Long?,
    val participated: Boolean?,
    val teamId: Long?,
    val visibility: GatheringVisible?,
    val progress: PeriodStatus?,
    val registration: PeriodStatus?,
)
