package sharev.gathering.application.port.inbound.usecase

import sharev.gathering.application.port.inbound.command.UpdateIntroduceTemplateCommand
import sharev.gathering.application.port.inbound.result.IntroduceTemplateResult

fun interface UpdateIntroduceTemplateUseCase {
    fun updateTemplate(command: UpdateIntroduceTemplateCommand): IntroduceTemplateResult
}
