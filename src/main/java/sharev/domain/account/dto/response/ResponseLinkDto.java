package sharev.domain.account.dto.response;

import sharev.domain.account.entity.Link;

public record ResponseLinkDto(
        Long id,
        String url
) {
    public static ResponseLinkDto fromEntity(Link link) {
        return new ResponseLinkDto(link.getId(), link.getLinkUrl());
    }
}
