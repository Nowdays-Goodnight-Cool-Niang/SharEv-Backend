package sharev.account.domain.model

data class OAuthAccount(
    val provider: OAuthProvider,
    val subjectIdentifier: String,
    val accountId: Long,
) {
}
