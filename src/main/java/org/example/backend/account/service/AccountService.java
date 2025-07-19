package org.example.backend.account.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.account.entity.Feedback;
import org.example.backend.account.repository.AccountRepository;
import org.example.backend.account.repository.FeedbackRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final AccountRepository accountRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional
    public void updateAccountInfo(Long accountId, String name, String email, String linkedinUrl, String githubUrl,
                                  String instagramUrl) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        account.updateInfo(name, email, linkedinUrl, githubUrl, instagramUrl);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String clientRegistrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        OAuth2AuthenticationToken oAuth2AuthenticationToken =
                new OAuth2AuthenticationToken(account, account.getAuthorities(), clientRegistrationId);

        SecurityContextHolder.getContext().setAuthentication(oAuth2AuthenticationToken);
    }

    @Transactional
    public void delete(Account account, String feedback) {
        accountRepository.delete(account);

        if (Objects.isNull(feedback) || feedback.isBlank()) {
            return;
        }

        feedbackRepository.save(new Feedback(feedback));
    }
}
