package sharev.exception;

import org.springframework.http.HttpStatus;

public abstract class BadRequestException extends CustomException {
    protected BadRequestException(ExceptionCode exceptionCode) {
        super(HttpStatus.BAD_REQUEST, exceptionCode);
    }
}
