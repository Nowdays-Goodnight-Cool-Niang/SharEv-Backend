package sharev.team.application.port.inbound.command

import sharev.team.domain.model.TeamType

data class CreateTeamCommand(
    val accountId: Long,
    val title: String?,
    val content: String,
    val type: TeamType,
) {
    init {
        require(type != TeamType.PUBLIC || !title.isNullOrBlank())
    }
}
