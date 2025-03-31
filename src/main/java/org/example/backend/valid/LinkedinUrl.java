package org.example.backend.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OptionalUrlValidator.LinkedInUrlValidator.class)
public @interface LinkedinUrl {
    String regexp() default "^www\\.linkedin\\.com/in/[\\w\\p{L}-]{3,30}$";

    String message() default "올바른 링크드인 URL 형식이 아닙니다. (www.linkedin.com/in/ 다음에 3-30자의 문자, 숫자, 하이픈만 허용)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
