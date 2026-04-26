package sharev.exception;

public class CustomException extends RuntimeException {
    public final ExceptionCode code;

    public CustomException(ExceptionCode exceptionCode) {
        super(exceptionCode.message);
        this.code = exceptionCode;
    }

    public CustomException(ExceptionCode exceptionCode, String message) {
        super(message);
        this.code = exceptionCode;
    }
}
