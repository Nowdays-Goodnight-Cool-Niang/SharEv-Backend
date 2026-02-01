package sharev.util;

import sharev.exception.ExceptionCode;
import sharev.exception.InternalServerException;

public class LockInterruptedException extends InternalServerException {
    public LockInterruptedException() {
        super(ExceptionCode.LOCK_INTERRUPTED);
    }
}
