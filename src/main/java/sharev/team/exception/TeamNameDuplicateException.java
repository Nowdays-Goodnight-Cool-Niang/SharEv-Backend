package sharev.team.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class TeamNameDuplicateException extends BadRequestException {
    public TeamNameDuplicateException() {
        super(ExceptionCode.TEAM_NAME_DUPLICATE);
    }
}



