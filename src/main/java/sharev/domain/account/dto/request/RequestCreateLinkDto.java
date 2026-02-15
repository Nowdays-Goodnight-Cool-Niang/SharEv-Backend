package sharev.domain.account.dto.request;

import org.hibernate.validator.constraints.URL;

public record RequestCreateLinkDto(
        @URL
        String url
) {
}
