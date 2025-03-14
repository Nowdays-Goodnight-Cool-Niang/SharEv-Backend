package org.example.backend.account.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.dto.request.RequestUpdateInfoDto;
import org.example.backend.account.entity.Account;
import org.example.backend.account.repository.AccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public void updateAccountInfo(UUID accountId, RequestUpdateInfoDto requestUpdateInfoDto) {
        Account accountEntity = accountRepository.findById(accountId)
                .orElseThrow();

        accountEntity.setName(requestUpdateInfoDto.name());
        accountEntity.setEmail(requestUpdateInfoDto.email());
        accountEntity.setLinkedinUrl(requestUpdateInfoDto.linkedinUrl());
        accountEntity.setGithubUrl(requestUpdateInfoDto.githubUrl());
        accountEntity.setInstagramUrl(requestUpdateInfoDto.instagramUrl());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String clientRegistrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        OAuth2AuthenticationToken oAuth2AuthenticationToken =
                new OAuth2AuthenticationToken(accountEntity, accountEntity.getAuthorities(), clientRegistrationId);

        SecurityContextHolder.getContext().setAuthentication(oAuth2AuthenticationToken);
    }
}
