package org.example.backend.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class OptionalUrlValidator<A extends Annotation> implements ConstraintValidator<A, String> {

    private Pattern pattern;

    protected abstract String getRegexp(A constraintAnnotation);

    @Override
    public void initialize(A constraintAnnotation) {
        this.pattern = Pattern.compile(getRegexp(constraintAnnotation));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        String processedValue = value;

        processedValue = cutPrefix(processedValue, "https://");
        processedValue = cutPrefix(processedValue, "www.");

        Matcher matcher = pattern.matcher(processedValue);
        return matcher.matches();
    }

    private static String cutPrefix(String processedValue, String protocol) {
        if (processedValue.startsWith(protocol)) {
            processedValue = processedValue.substring(protocol.length());
        }

        return processedValue;
    }

    public static class GithubUrlValidator extends OptionalUrlValidator<GithubUrl> {
        @Override
        protected String getRegexp(GithubUrl constraintAnnotation) {
            return constraintAnnotation.regexp();
        }
    }

    public static class InstagramUrlValidator extends OptionalUrlValidator<InstagramUrl> {
        @Override
        protected String getRegexp(InstagramUrl constraintAnnotation) {
            return constraintAnnotation.regexp();
        }
    }

    public static class LinkedInUrlValidator extends OptionalUrlValidator<LinkedinUrl> {
        @Override
        protected String getRegexp(LinkedinUrl constraintAnnotation) {
            return constraintAnnotation.regexp();
        }
    }
}
