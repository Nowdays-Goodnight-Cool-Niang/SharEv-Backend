package sharev.link.application.port.outbound

import sharev.link.domain.model.Link

interface SaveLinkPort {
    fun save(accountId: Long, url: String): Link
    fun saveAll(accountId: Long, urls: Set<String>)
}
