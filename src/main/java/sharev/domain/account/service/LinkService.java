package sharev.domain.account.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sharev.domain.account.dto.response.ResponseCreateLinkDto;
import sharev.domain.account.dto.response.ResponseLinkDto;
import sharev.domain.account.entity.Account;
import sharev.domain.account.entity.Link;
import sharev.domain.account.exception.LinkNotFoundException;
import sharev.domain.account.repository.LinkRepository;
import sharev.exception.AccessDeniedException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LinkService {
    private final LinkRepository linkRepository;

    @Transactional
    public ResponseCreateLinkDto addLink(Account account, String url) {
        Link link = linkRepository.save(new Link(account, url));
        return new ResponseCreateLinkDto(link.getId(), link.getLinkUrl());
    }

    public List<ResponseLinkDto> getAllLinks(Account account) {
        return linkRepository.findAllByAccountId(account.getId()).stream()
                .map(ResponseLinkDto::fromEntity)
                .toList();
    }

    @Transactional
    public void removeLink(Account account, Long linkId) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(LinkNotFoundException::new);

        if (account.getId().equals(link.getAccount().getId())) {
            linkRepository.delete(link);
            return;
        }

        throw new AccessDeniedException();
    }
}
