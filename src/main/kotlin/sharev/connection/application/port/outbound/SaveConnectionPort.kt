package sharev.connection.application.port.outbound

fun interface SaveConnectionPort {
    fun save(myCardId: Long, otherCardId: Long)
}
