package sharev.member.domain.model

data class Member(
    val id: Long,
    val teamId: Long,
    val accountId: Long,
    val accountName: String,
    val accountEmail: String,
    val status: MemberStatus,
    val role: MemberRole,
) {
}
