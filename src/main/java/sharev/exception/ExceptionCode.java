package sharev.exception;

import static sharev.exception.ErrorCategory.BAD_REQUEST;
import static sharev.exception.ErrorCategory.CONFLICT;
import static sharev.exception.ErrorCategory.FORBIDDEN;
import static sharev.exception.ErrorCategory.INTERNAL;
import static sharev.exception.ErrorCategory.NOT_FOUND;

public enum ExceptionCode {
    JOIN_ALREADY(CONFLICT, "이미 행사에 가입하셨습니다."),
    REGISTER_ALREADY(CONFLICT, "이미 도감에 등록된 명함입니다."),
    REGISTER_MYSELF(BAD_REQUEST, "자기 자신을 도감에 등록할 수 없습니다."),
    TEAM_NAME_DUPLICATE(CONFLICT, "이미 존재하는 팀명입니다."),
    WRONG_TEMPLATE(BAD_REQUEST, "불일치하는 템플릿 content와 placeholder가 존재합니다. 다시 확인해 주세요."),

    CARD_NOT_FOUND(NOT_FOUND, "명함이 존재하지 않습니다."),
    CARD_UNCOMPLETED(BAD_REQUEST, "명함이 완성되지 않았습니다."),
    ACCOUNT_NOT_FOUND(NOT_FOUND, "사용자가 존재하지 않습니다."),
    EVENT_NOT_FOUND(NOT_FOUND, "이벤트가 존재하지 않습니다."),
    INTRODUCE_TEMPLATE_NOT_FOUND(NOT_FOUND, "자기소개 템플릿이 존재하지 않습니다."),
    TEAM_NOT_FOUND(NOT_FOUND, "팀이 존재하지 않습니다."),
    INVALID_INTRODUCE_TEMPLATE(BAD_REQUEST, "작성된 소개문이 템플릿과 일치하지 않습니다."),
    LINK_NOT_FOUND(NOT_FOUND, "링크가 존재하지 않습니다."),

    LOCK_INTERRUPTED(INTERNAL, "예상치 못 한 오류가 발생했습니다. 운영진에게 문의해주세요."),
    LOCK_OVER_WAIT_TIME(INTERNAL, "예상치 못 한 오류가 발생했습니다. 운영진에게 문의해주세요."),
    KEY_BLANK(BAD_REQUEST, "pin number 확인 도중 예외가 발생했습니다. 운영진에게 알려 주십시오."),
    PIN_NUMBER_GENERATE(BAD_REQUEST, "pin number 발급 도중 예외가 발생했습니다. 운영진에게 알려 주십시오."),

    ACCESS_DENIED(FORBIDDEN, "권한이 존재하지 않습니다."),

    MEMBER_NOT_FOUND(NOT_FOUND, "멤버를 찾을 수 없습니다."),
    MEMBER_ALREADY_EXISTS(CONFLICT, "이미 팀에 존재하는 멤버입니다."),
    MEMBER_NOT_INVITED(BAD_REQUEST, "초대 상태가 아닙니다."),
    CANNOT_REMOVE_SELF(BAD_REQUEST, "본인을 제거할 수 없습니다. 탈퇴를 이용해주세요."),
    CANNOT_REMOVE_LAST_ADMIN(BAD_REQUEST, "마지막 관리자는 제거할 수 없습니다."),
    ;

    public final ErrorCategory errorCategory;
    public final String message;

    ExceptionCode(ErrorCategory errorCategory, String message) {
        this.errorCategory = errorCategory;
        this.message = message;
    }
}
