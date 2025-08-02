package org.example.backend.account.dto.response;

import lombok.Getter;
import org.example.backend.account.entity.Account;

@Getter
public class ResponseAccountInfo {
    private final Long id;
    private final String name;
    private final String email;
    private final String linkedinUrl;
    private final String githubUrl;
    private final String instagramUrl;

    public ResponseAccountInfo(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.email = account.getEmail();
        this.linkedinUrl = account.getLinkedinUrl();
        this.githubUrl = account.getGithubUrl();
        this.instagramUrl = account.getInstagramUrl();
    }
}
