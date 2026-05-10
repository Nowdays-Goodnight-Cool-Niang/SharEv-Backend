package sharev.member.adapter.inbound.web.dto.response

data class MemberResponse(
    val memberId: Long,
    val name: String,
    val email: String,
    val role: String,
    val status: String,
)
