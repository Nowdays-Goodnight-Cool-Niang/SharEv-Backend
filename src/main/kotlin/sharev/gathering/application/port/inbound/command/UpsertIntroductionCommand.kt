package sharev.gathering.application.port.inbound.command

import sharev.gathering.domain.model.FieldSpec
import java.util.*

data class UpsertIntroductionCommand(
    val accountId: Long,
    val gatheringId: UUID,
    val source: String,
    val fields: Map<String, FieldSpec>,
)
