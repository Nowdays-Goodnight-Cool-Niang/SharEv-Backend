package sharev.common.domain.exception

data class ExceptionDetails(
    val category: ExceptionCategory,
    val code: String,
    val message: String,
) {
}
