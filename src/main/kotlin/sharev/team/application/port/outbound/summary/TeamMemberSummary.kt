package sharev.team.application.port.outbound.summary

import sharev.member.domain.model.MemberRole

data class TeamMemberSummary(
    val name: String,
    val email: String,
    val role: MemberRole,
)
