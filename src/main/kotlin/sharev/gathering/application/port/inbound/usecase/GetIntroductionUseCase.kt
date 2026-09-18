package sharev.gathering.application.port.inbound.usecase

import sharev.gathering.application.port.inbound.result.IntroductionResult
import java.util.*

fun interface GetIntroductionUseCase {
    fun getLatestIntroduction(gatheringId: UUID, accountId: Long): IntroductionResult
}
