package sharev.account.domain.model

enum class OAuthProvider {
    KAKAO,
    GOOGLE,
    ;

    companion object {
        fun fromRegistrationId(registrationId: String): OAuthProvider? {
            return entries.firstOrNull {
                it.name.equals(registrationId, ignoreCase = true)
            }
        }
    }
}
