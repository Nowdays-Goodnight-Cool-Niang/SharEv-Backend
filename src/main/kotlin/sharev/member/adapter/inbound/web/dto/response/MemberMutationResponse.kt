package sharev.member.adapter.inbound.web.dto.response

data class InviteMemberResponse(
    val memberId: Long,
    val role: String,
    val status: String,
)
