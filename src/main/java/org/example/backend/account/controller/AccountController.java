package org.example.backend.account.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.dto.request.RequestUpdateInfoDto;
import org.example.backend.account.dto.response.ResponseAccountIdDto;
import org.example.backend.account.dto.response.ResponseAccountInfo;
import org.example.backend.account.entity.Account;
import org.example.backend.account.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @PatchMapping
    public ResponseEntity<Void> updateAccountInfo(@AuthenticationPrincipal Account account,
                                                  @RequestBody RequestUpdateInfoDto requestUpdateInfoDto) {

        accountService.updateAccountInfo(account.getId(), requestUpdateInfoDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<ResponseAccountInfo> getAccountInfo(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(new ResponseAccountInfo(account));
    }

    @GetMapping("my-id")
    public ResponseEntity<ResponseAccountIdDto> getMyId(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(new ResponseAccountIdDto(account.getId()));
    }
}
