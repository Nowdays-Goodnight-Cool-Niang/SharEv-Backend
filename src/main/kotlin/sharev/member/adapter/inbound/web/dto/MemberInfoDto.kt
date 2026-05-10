package sharev.member.adapter.inbound.web.dto

import sharev.member.domain.model.MemberRole

data class MemberInfoDto(
    val memberId: Long,
    val name: String,
    val memberRole: MemberRole,
)
