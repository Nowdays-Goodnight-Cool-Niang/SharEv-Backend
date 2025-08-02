package org.example.backend.account.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.account.entity.Feedback;
import org.example.backend.account.exception.AccountNotFoundException;
import org.example.backend.account.repository.AccountRepository;
import org.example.backend.account.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final AccountRepository accountRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional
    public Account updateAccountInfo(Long accountId, String name, String email, String linkedinUrl,
                                     String githubUrl, String instagramUrl) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        account.updateInfo(name, email, linkedinUrl, githubUrl, instagramUrl);

        return account;
    }

    @Transactional
    public void delete(Account account) {
        accountRepository.delete(account);
    }

    @Transactional
    public void saveFeedback(String feedback) {
        if (Objects.isNull(feedback) || feedback.isBlank()) {
            return;
        }

        feedbackRepository.save(new Feedback(feedback));
    }
}
