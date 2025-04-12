package org.example.backend.socialDex.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.account.repository.AccountRepository;
import org.example.backend.socialDex.dto.response.ResponseSocialDexDto;
import org.example.backend.socialDex.dto.response.ResponseSocialDexInfoDto;
import org.example.backend.socialDex.entity.SocialDex;
import org.example.backend.socialDex.repository.SocialDexRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialDexService {

    private final SocialDexRepository socialDexRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public ResponseSocialDexDto updateSocialDex(UUID firstAccountId, UUID secondAccountId) {
        Account firstAccount = accountRepository.findById(firstAccountId)
                .orElseThrow();
        Account secondAccount = accountRepository.findById(secondAccountId)
                .orElseThrow();

        SocialDex socialDex = new SocialDex(firstAccount, secondAccount);
        SocialDex.SocialDexId socialDexId = socialDex.getId();

        try {
            socialDexRepository.save(socialDex);
        } catch (DataIntegrityViolationException e) {
            // ignore
        }

        return new ResponseSocialDexDto(socialDexId.getFirstAccountId(), socialDexId.getSecondAccountId());
    }

    public ResponseSocialDexInfoDto getSocialDex(UUID accountId, LocalDateTime snapshotTime, Pageable pageable) {
        Long registerCount = socialDexRepository.getRegisterCount(accountId, snapshotTime);
        Page<ResponseSocialDexInfoDto.AccountInfo> accountInfoPage = socialDexRepository.findDexParticipants(accountId, snapshotTime, pageable);
        Page<ResponseSocialDexInfoDto.SocialDexInfo> socialDexInfoPage = accountInfoPage.map(ResponseSocialDexInfoDto.AccountInfo::convertSocialDexInfo);

        return new ResponseSocialDexInfoDto(registerCount, socialDexInfoPage);
    }
}
