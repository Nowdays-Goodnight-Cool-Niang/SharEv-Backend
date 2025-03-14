package org.example.backend.shareCard.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.shareCard.dto.request.RequestUpdateShareCardDto;
import org.example.backend.shareCard.dto.response.ResponseShareCardDto;
import org.example.backend.shareCard.service.ShareCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/share-cards")
public class ShareCardController {

    private final ShareCardService shareCardService;

    @PatchMapping
    public ResponseEntity<ResponseShareCardDto> updateShareCard(@AuthenticationPrincipal Account account,
                                                                @RequestBody RequestUpdateShareCardDto requestUpdateShareCardDto) {
        shareCardService.updateShareCard(account.getId(), requestUpdateShareCardDto.teamName(),
                requestUpdateShareCardDto.position(), requestUpdateShareCardDto.introductionText());

        ResponseShareCardDto responseShareCardDto = shareCardService.getShareCard(account.getId());
        return ResponseEntity.ok(responseShareCardDto);
    }

    @GetMapping
    public ResponseEntity<ResponseShareCardDto> getShareCard(@AuthenticationPrincipal Account account) {
        ResponseShareCardDto responseShareCardDto = shareCardService.getShareCard(account.getId());
        return ResponseEntity.ok(responseShareCardDto);
    }

    @GetMapping("/{participantsId}")
    public ResponseEntity<ResponseShareCardDto> getShareCard(@PathVariable(name = "participantsId") UUID participantsId) {
        ResponseShareCardDto responseShareCardDto = shareCardService.getShareCard(participantsId);
        return ResponseEntity.ok(responseShareCardDto);
    }
}
