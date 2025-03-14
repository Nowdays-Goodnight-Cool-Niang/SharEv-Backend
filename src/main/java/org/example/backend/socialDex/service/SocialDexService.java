package org.example.backend.socialDex.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.account.repository.AccountRepository;
import org.example.backend.socialDex.dto.response.ResponseSocialDexDto;
import org.example.backend.socialDex.entity.SocialDex;
import org.example.backend.socialDex.repository.SocialDexRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        SocialDex socialDex = socialDexRepository.save(new SocialDex(firstAccount, secondAccount));

        SocialDex.SocialDexId socialDexId = socialDex.getId();
        return new ResponseSocialDexDto(socialDexId.getFirstAccountId(), socialDexId.getFirstAccountId());
    }
}
