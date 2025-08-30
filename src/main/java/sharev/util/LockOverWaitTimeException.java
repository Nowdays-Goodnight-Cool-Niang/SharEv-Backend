package sharev.util;

import sharev.exception.ExceptionCode;
import sharev.exception.InternalServerException;

public class LockOverWaitTimeException extends InternalServerException {
    public LockOverWaitTimeException() {
        super(ExceptionCode.LOCK_OVER_WAIT_TIME);
    }
}
