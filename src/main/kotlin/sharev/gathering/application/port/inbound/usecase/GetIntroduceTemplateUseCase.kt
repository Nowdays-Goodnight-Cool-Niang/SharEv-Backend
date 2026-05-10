package sharev.gathering.application.port.inbound.usecase

import sharev.gathering.application.port.inbound.result.IntroduceTemplateResult
import java.util.*

fun interface GetIntroduceTemplateUseCase {
    fun getLatestTemplate(gatheringId: UUID, accountId: Long): IntroduceTemplateResult
}
