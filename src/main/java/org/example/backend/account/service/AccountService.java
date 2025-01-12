package org.example.backend.account.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.dto.request.RequestUpdateInfoDto;
import org.example.backend.account.entity.Account;
import org.example.backend.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public void updateAccountInfo(Account account, RequestUpdateInfoDto requestUpdateInfoDto) {
        Account accountEntity = accountRepository.findById(account.getId()).orElseThrow();
        accountEntity.setName(requestUpdateInfoDto.name());
        accountEntity.setPhone(requestUpdateInfoDto.phone());
        accountEntity.setProfileImageId(requestUpdateInfoDto.profileImageId());
        accountEntity.setGithubUrl(requestUpdateInfoDto.githubUrl());
        accountEntity.setInstagramUrl(requestUpdateInfoDto.instagramUrl());
        accountEntity.setFacebookUrl(requestUpdateInfoDto.facebookUrl());
    }
}
