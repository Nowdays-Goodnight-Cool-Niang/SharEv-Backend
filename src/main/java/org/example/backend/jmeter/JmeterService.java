package org.example.backend.jmeter;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JmeterService {

    private final AccountRepository accountRepository;

    @Transactional
    public JmeterSignupResponse signup(Long kakaoId) {
        Account account = accountRepository.save(new Account(kakaoId, "테스트" + kakaoId, "test" + kakaoId + "@test.com"));
        return new JmeterSignupResponse(account.getKakaoOauthId());
    }

    public Account login(Long kakaoId) {
        return accountRepository.findByKakaoOauthId(kakaoId)
                .orElseThrow();
    }
}
