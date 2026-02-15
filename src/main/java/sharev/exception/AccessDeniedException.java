package sharev.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends CustomException {
    public AccessDeniedException() {
        super(HttpStatus.FORBIDDEN, ExceptionCode.ACCESS_DENIED);
    }
}
