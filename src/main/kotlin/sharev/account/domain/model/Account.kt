package sharev.account.domain.model

data class Account(
    val id: Long,
    val name: String,
    val email: String,
    val role: AccountRole,
) {
    fun updateInfo(name: String, email: String): Account {

        require(name.isNotBlank()) { "이름은 필수입니다." }

        return copy(name = name, email = email)
    }
}
