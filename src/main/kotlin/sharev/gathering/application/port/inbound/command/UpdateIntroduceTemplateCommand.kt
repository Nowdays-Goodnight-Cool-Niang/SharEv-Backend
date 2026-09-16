package sharev.gathering.application.port.inbound.command

import java.util.*

data class UpdateIntroduceTemplateCommand(
    val gatheringId: UUID,
    val content: String,
    val placeholders: Map<String, String>,
) {
}
