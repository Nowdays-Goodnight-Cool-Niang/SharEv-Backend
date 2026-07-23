package sharev.common.adapter.inbound.web.advice

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class InvalidRequestExceptionAdvice {

    @ExceptionHandler(BindException::class)
    fun handleBindException(exception: BindException): ResponseEntity<MultiValueMap<String, String>> {
        val errorMessages = LinkedMultiValueMap<String, String>()

        exception.fieldErrors.forEach { fieldError ->
            errorMessages.add(fieldError.field, calculateDefaultMessage(fieldError))
        }

        return ResponseEntity.badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorMessages)
    }

    private fun calculateDefaultMessage(fieldError: FieldError): String {
        if (fieldError.isBindingFailure) {
            return "유효한 값이 아닙니다."
        }

        return fieldError.defaultMessage
            ?: "조건이 충족되지 않았습니다."
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(exception: HttpMessageNotReadableException): ResponseEntity<MultiValueMap<String, String>> {
        val errorMessages = LinkedMultiValueMap<String, String>()
        val cause = exception.cause

        if (cause is MismatchedInputException && cause.path.isNotEmpty()) {
            val field = cause.path.lastOrNull()?.fieldName ?: "request"
            errorMessages.add(field, "필수 값이거나 형식이 올바르지 않습니다.")
        } else {
            errorMessages.add("request", "요청 본문을 해석할 수 없습니다.")
        }

        return ResponseEntity.badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorMessages)
    }
}

