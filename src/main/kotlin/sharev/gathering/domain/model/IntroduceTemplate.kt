package sharev.gathering.domain.model

data class IntroduceTemplate(
    val id: Long,
    val gatheringId: java.util.UUID,
    val version: Int,
    val content: IntroduceTemplateContent,
)
