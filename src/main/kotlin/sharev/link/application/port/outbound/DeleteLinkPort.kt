package sharev.link.application.port.outbound

interface DeleteLinkPort {
    fun delete(linkId: Long)
    fun deleteAllByIds(accountId: Long, linkIds: Set<Long>)
}
