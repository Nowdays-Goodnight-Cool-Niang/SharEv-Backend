package sharev.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sharev.exception.CustomException;
import sharev.exception.ErrorCategory;

@RestControllerAdvice
public class CustomExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleBindException(CustomException customException) {
        ErrorResponse errorResponse = new ErrorResponse(customException.code.name(), customException.getMessage());

        return ResponseEntity.status(mapToHttpStatus(customException.code.errorCategory))
                .body(errorResponse);
    }

    private HttpStatus mapToHttpStatus(ErrorCategory category) {
        return switch (category) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case INTERNAL -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
