package sharev.feedback.application.port.outbound

fun interface SaveFeedbackPort {
    fun save(content: String)
}
