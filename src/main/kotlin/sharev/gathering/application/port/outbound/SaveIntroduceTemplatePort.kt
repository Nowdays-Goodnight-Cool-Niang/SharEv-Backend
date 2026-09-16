package sharev.gathering.application.port.outbound

import sharev.gathering.domain.model.IntroduceTemplate

fun interface SaveIntroduceTemplatePort {
    fun save(introduceTemplate: IntroduceTemplate): IntroduceTemplate
}
