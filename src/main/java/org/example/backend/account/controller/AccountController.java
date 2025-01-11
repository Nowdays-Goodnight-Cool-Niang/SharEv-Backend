package org.example.backend.account.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.dto.request.RequestUpdateInfoDto;
import org.example.backend.account.entity.Account;
import org.example.backend.account.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @PatchMapping
    public ResponseEntity<Void> updateAccountInfo(@AuthenticationPrincipal Account account,
                                                  RequestUpdateInfoDto requestUpdateInfoDto) {

        accountService.updateAccountInfo(account, requestUpdateInfoDto);
        return ResponseEntity.ok().build();
    }
}
