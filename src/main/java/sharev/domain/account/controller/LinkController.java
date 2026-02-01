package sharev.domain.account.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sharev.domain.account.dto.request.RequestCreateLinkDto;
import sharev.domain.account.dto.request.RequestDeleteLinkDto;
import sharev.domain.account.dto.response.ResponseCreateLinkDto;
import sharev.domain.account.dto.response.ResponseLinkDto;
import sharev.domain.account.entity.Account;
import sharev.domain.account.service.LinkService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts/links")
public class LinkController {
    private final LinkService linkService;

    @PostMapping
    public ResponseEntity<ResponseCreateLinkDto> addLink(@AuthenticationPrincipal Account account,
                                                         RequestCreateLinkDto requestCreateLinkDto) {

        ResponseCreateLinkDto responseCreateLinkDto = linkService.addLink(account, requestCreateLinkDto.url());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseCreateLinkDto);
    }

    @GetMapping
    public ResponseEntity<List<ResponseLinkDto>> getAllLinks(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(linkService.getAllLinks(account));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteLink(@AuthenticationPrincipal Account account,
                                           RequestDeleteLinkDto requestDeleteLinkDto) {

        linkService.removeLink(account, requestDeleteLinkDto.linkId());

        return ResponseEntity.noContent()
                .build();
    }
}
