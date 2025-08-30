package sharev.advice;

import java.util.HashMap;
import java.util.Map;
import sharev.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, String>> handleBindException(CustomException exception) {
        Map<String, String> body = new HashMap<>();
        body.put("code", exception.exceptionCode.name());
        body.put("message", exception.exceptionCode.message);

        return ResponseEntity.status(exception.status)
                .body(body);
    }
}
