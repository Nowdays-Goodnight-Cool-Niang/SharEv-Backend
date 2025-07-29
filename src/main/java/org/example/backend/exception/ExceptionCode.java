package org.example.backend.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExceptionCode {
    JOIN_ALREADY("이미 행사에 가입하셨습니다."),
    REGISTER_ALREADY("이미 도감에 등록된 프로필입니다."),
    REGISTER_MYSELF("자기 자신을 도감에 등록할 수 없습니다."),

    PROFILE_NOT_FOUND("프로필이 존재하지 않습니다."),
    ACCOUNT_NOT_FOUND("사용자가 존재하지 않습니다."),
    EVENT_NOT_FOUND("이벤트가 존재하지 않습니다."),

    LOCK_INTERRUPTED("예상치 못 한 오류가 발생했습니다. 운영진에게 문의해주세요."),
    LOCK_OVER_WAIT_TIME("예상치 못 한 오류가 발생했습니다. 운영진에게 문의해주세요."),
    KEY_BLANK("pin number 확인 도중 예외가 발생했습니다. 운영진에게 알려 주십시오."),
    PIN_NUMBER_GENERATE("pin number 발급 도중 예외가 발생했습니다. 운영진에게 알려 주십시오.");

    public final String message;
}
