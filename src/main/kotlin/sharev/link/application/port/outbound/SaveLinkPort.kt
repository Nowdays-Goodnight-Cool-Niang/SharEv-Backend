package sharev.link.application.port.outbound

import sharev.link.domain.model.Link

fun interface SaveLinkPort {
    fun save(accountId: Long, url: String): Link
}
