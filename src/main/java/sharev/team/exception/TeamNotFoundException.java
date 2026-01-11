package sharev.team.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class TeamNotFoundException extends BadRequestException {
    public TeamNotFoundException() {
        super(ExceptionCode.TEAM_NOT_FOUND);
    }
}
