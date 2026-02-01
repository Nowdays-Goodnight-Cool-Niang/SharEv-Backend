package sharev.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExceptionCode {
    JOIN_ALREADY("이미 행사에 가입하셨습니다."),
    REGISTER_ALREADY("이미 도감에 등록된 명함입니다."),
    REGISTER_MYSELF("자기 자신을 도감에 등록할 수 없습니다."),
    TEAM_NAME_DUPLICATE("이미 존재하는 팀명입니다."),
    WRONG_TEMPLATE("불일치하는 템플릿 content와 placeholder가 존재합니다. 다시 확인해 주세요."),

    CARD_NOT_FOUND("명함이 존재하지 않습니다."),
    CARD_UNCOMPLETED("명함이 완성되지 않았습니다."),
    ACCOUNT_NOT_FOUND("사용자가 존재하지 않습니다."),
    EVENT_NOT_FOUND("이벤트가 존재하지 않습니다."),
    INTRODUCE_TEMPLATE_NOT_FOUND("자기소개 템플릿이 존재하지 않습니다."),
    TEAM_NOT_FOUND("팀이 존재하지 않습니다."),
    INVALID_INTRODUCE_TEMPLATE("작성된 소개문이 템플릿과 일치하지 않습니다."),
    LINK_NOT_FOUND("링크가 존재하지 않습니다."),

    LOCK_INTERRUPTED("예상치 못 한 오류가 발생했습니다. 운영진에게 문의해주세요."),
    LOCK_OVER_WAIT_TIME("예상치 못 한 오류가 발생했습니다. 운영진에게 문의해주세요."),
    KEY_BLANK("pin number 확인 도중 예외가 발생했습니다. 운영진에게 알려 주십시오."),
    PIN_NUMBER_GENERATE("pin number 발급 도중 예외가 발생했습니다. 운영진에게 알려 주십시오."),

    ACCESS_DENIED("권한이 존재하지 않습니다"),
    ;

    public final String message;
}
