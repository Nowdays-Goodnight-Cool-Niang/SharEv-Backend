package sharev.common.application.port.outbound

fun interface PublishEventPort {
    fun publish(event: Any)
}
