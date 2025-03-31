package org.example.backend.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OptionalUrlValidator.InstagramUrlValidator.class)
public @interface InstagramUrl {
    String regexp() default "^instagram\\.com/[a-zA-Z0-9._]{1,30}$";

    String message() default "유효한 인스타그램 프로필 URL 형식이 아닙니다. (instagram.com/ 다음에 1-30자의 영문, 숫자, 밑줄, 마침표만 허용)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
