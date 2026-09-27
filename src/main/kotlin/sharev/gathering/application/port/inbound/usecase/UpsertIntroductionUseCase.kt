package sharev.gathering.application.port.inbound.usecase

import sharev.gathering.application.port.inbound.command.UpsertIntroductionCommand
import sharev.gathering.application.port.inbound.result.IntroductionResult

fun interface UpsertIntroductionUseCase {
    fun upsertIntroduction(command: UpsertIntroductionCommand): IntroductionResult
}
