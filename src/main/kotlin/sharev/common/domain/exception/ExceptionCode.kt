package sharev.common.domain.exception

interface ExceptionCode {
    val category: ExceptionCategory
    val name: String
    val message: String

    fun toDetails() = ExceptionDetails(category, name, message)
}
