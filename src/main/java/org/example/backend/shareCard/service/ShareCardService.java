package org.example.backend.shareCard.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.account.repository.AccountRepository;
import org.example.backend.shareCard.dto.response.ResponseShareCardDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShareCardService {

    private final AccountRepository accountRepository;

    @Transactional
    public void updateShareCard(UUID accountId, String teamName, String position, String introductionText) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        account.setTeamName(teamName);
        account.setPosition(position);
        account.setIntroductionText(introductionText);
    }

    public ResponseShareCardDto getShareCard(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        return new ResponseShareCardDto(account.getTeamName(), account.getPosition(), account.getIntroductionText());
    }
}
