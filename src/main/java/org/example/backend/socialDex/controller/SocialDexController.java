package org.example.backend.socialDex.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.socialDex.dto.request.RequestUpdateSocialDexDto;
import org.example.backend.socialDex.dto.response.ResponseSocialDexDto;
import org.example.backend.socialDex.dto.response.ResponseSocialDexInfoDto;
import org.example.backend.socialDex.service.SocialDexService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/participants")
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

    @GetMapping
    @Secured("ROLE_USER")
    public ResponseEntity<Page<ResponseSocialDexInfoDto>> getSocialDex(@AuthenticationPrincipal Account account,
                                                                       @RequestParam("snapshotTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime snapshotTime,
                                                                       Pageable pageable) {
        return ResponseEntity.ok(socialDexService.getSocialDex(account.getId(), snapshotTime, pageable));
    }
}
