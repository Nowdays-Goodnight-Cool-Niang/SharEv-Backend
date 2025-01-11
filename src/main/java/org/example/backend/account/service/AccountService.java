package org.example.backend.account.service;

import org.example.backend.account.dto.request.RequestUpdateInfoDto;
import org.example.backend.account.entity.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AccountService {
    public void updateAccountInfo(Account account, RequestUpdateInfoDto requestUpdateInfoDto) {
        account.setName(requestUpdateInfoDto.name());
        account.setPhone(requestUpdateInfoDto.phone());
        account.setProfileImageId(requestUpdateInfoDto.profileImageId());
        account.setGithubUrl(requestUpdateInfoDto.githubUrl());
        account.setInstagramUrl(requestUpdateInfoDto.instagramUrl());
        account.setFacebookUrl(requestUpdateInfoDto.facebookUrl());
    }
}
