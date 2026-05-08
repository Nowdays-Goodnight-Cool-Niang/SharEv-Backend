package sharev.common.adapter.inbound.web.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import sharev.common.adapter.inbound.web.dto.ExceptionResponse
import sharev.common.adapter.inbound.web.mapper.toResponse
import sharev.common.domain.exception.BusinessException
import sharev.common.domain.exception.ExceptionCategory

@RestControllerAdvice
class BusinessExceptionAdvice {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(businessException: BusinessException): ResponseEntity<ExceptionResponse> {
        return ResponseEntity.status(mapToHttpStatus(businessException.details.category))
            .body(businessException.details.toResponse())
    }

    private fun mapToHttpStatus(exceptionCategory: ExceptionCategory): HttpStatus {
        return when (exceptionCategory) {
            ExceptionCategory.NOT_FOUND -> HttpStatus.NOT_FOUND
            ExceptionCategory.CONFLICT -> HttpStatus.CONFLICT
            ExceptionCategory.FORBIDDEN -> HttpStatus.FORBIDDEN
            ExceptionCategory.BAD_REQUEST -> HttpStatus.BAD_REQUEST
            ExceptionCategory.INTERNAL -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}
