package sharev.link.domain.model

data class Link(
    val id: Long,
    val accountId: Long,
    val url: String,
) {
    companion object {
        const val MAX_COUNT = 3
    }
}
