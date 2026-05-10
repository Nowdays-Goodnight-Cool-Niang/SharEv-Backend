package sharev.link.application.port.outbound

import sharev.link.domain.model.Link

interface LoadLinkPort {
    fun load(linkId: Long): Link
    fun loadAllByAccountId(accountId: Long): List<Link>
    fun loadAllByAccountIdIn(accountIds: Collection<Long>): List<Link>
}
