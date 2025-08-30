package sharev.card_connection.dto.response;

import sharev.util.Type;

public record NonConnectedCardDto(
        Type type,
        String name,
        String proudestExperience,
        int iconNumber,
        boolean relationFlag
) implements ResponseConnectionCardDto {

    public NonConnectedCardDto(String name, int iconNumber, String proudestExperience) {
        this(Type.MINIMUM, name, proudestExperience, iconNumber, false);
    }
}
