package org.example.backend.socialDex.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.socialDex.dto.request.RequestUpdateSocialDexDto;
import org.example.backend.socialDex.dto.response.ResponseSocialDexDto;
import org.example.backend.socialDex.service.SocialDexService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/social-dex")
@RequiredArgsConstructor
public class SocialDexController {

    private final SocialDexService socialDexService;

    @PostMapping
    public ResponseEntity<ResponseSocialDexDto> updateSocialDex(@AuthenticationPrincipal Account account,
                                                                @RequestBody RequestUpdateSocialDexDto requestUpdateSocialDexDto) {
        ResponseSocialDexDto responseSocialDexDto =
                socialDexService.updateSocialDex(account.getId(), requestUpdateSocialDexDto.targetAccountId());

        return ResponseEntity.ok(responseSocialDexDto);
    }
}
