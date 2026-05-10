package sharev.account.domain.event

data class AccountLinkUpdatedEvent(
    val accountId: Long,
    val addLinkUrls: Set<String>,
    val deleteLinkIds: Set<Long>,
)
