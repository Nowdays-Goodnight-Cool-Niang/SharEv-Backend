package sharev.common.domain.exception

abstract class BusinessException(
    val details: ExceptionDetails
) : RuntimeException(details.message) {
}
