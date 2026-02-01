package sharev.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OptionalUrlValidator.GithubUrlValidator.class)
public @interface GithubUrl {
    String regexp() default "^github\\.com/[a-zA-Z0-9-]{1,39}$";

    String message() default "유효한 깃허브 프로필 URL 형식이 아닙니다. (github.com/ 다음에 1-39자의 문자, 숫자, 하이픈만 허용)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
