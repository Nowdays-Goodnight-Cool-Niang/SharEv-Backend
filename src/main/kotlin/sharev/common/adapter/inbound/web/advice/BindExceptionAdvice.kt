package sharev.common.adapter.inbound.web.advice

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class BindExceptionAdvice {

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
}

