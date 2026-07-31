package sharev.account.domain.model

data class Account(
    val id: Long,
    val name: String,
    val email: String,
    val role: AccountRole,
    val handle: String?,
) {
}

object HandleValidator {
    const val REGEX_PATTERN = "^[a-zA-Z0-9_]{4,20}$"
    const val REGEX_MESSAGE = "유효하지 않은 핸들 형식입니다. 영어, 숫자, 언더바를 사용하여 4~20 길이로 구성해 주세요."
    private val regex = Regex(REGEX_PATTERN)

    fun isValid(handle: String): Boolean {
        return regex.matches(handle)
    }
}
