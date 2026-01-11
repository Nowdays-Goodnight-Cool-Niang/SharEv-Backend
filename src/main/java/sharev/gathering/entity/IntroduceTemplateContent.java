package sharev.gathering.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sharev.gathering.exception.TemplateVariableMismatchException;

public record IntroduceTemplateContent(
        @NotBlank
        String text,

        @NotNull
        Map<String, String> fieldPlaceholders) {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    public IntroduceTemplateContent {
        validateContentAndFieldPlaceholders(text, fieldPlaceholders);
    }

    private static void validateContentAndFieldPlaceholders(String text, Map<String, String> fieldPlaceholders) {
        Matcher matcher = VARIABLE_PATTERN.matcher(text);
        Set<String> foundKeys = new HashSet<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            foundKeys.add(key);

            if (!fieldPlaceholders.containsKey(key)) {
                throw new TemplateVariableMismatchException();
            }
        }

        if (foundKeys.size() != fieldPlaceholders.size()) {
            throw new TemplateVariableMismatchException();
        }
    }

    public boolean hasSameFields(Map<String, String> fieldPlaceholders) {
        Set<String> fields = this.fieldPlaceholders.keySet();
        Set<String> otherFields = fieldPlaceholders.keySet();

        return fields.size() == otherFields.size() && fields.containsAll(otherFields);
    }

    public Set<String> getFields() {
        return fieldPlaceholders.keySet();
    }
}
