package sharev.link.application.port.outbound

fun interface DeleteLinkPort {
    fun delete(linkId: Long)
}
