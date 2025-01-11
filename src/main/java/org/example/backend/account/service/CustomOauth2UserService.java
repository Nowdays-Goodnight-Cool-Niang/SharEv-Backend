package org.example.backend.account.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.account.repository.AccountRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
    private final AccountRepository accountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
        Long id = oAuth2User.getAttribute("id");

        if (id == null) {
            throw new OAuth2AuthenticationException("로그인 도중 오류가 발생했습니다. 운영진에게 문의해주세요.");
        }

        return accountRepository.findById(id).orElseGet(() -> this.createAccount(oAuth2User, id));
    }

    private Account createAccount(OAuth2User oAuth2User, Long id) {
        return this.accountRepository.save(new Account(id, this.getNickname(oAuth2User)));
    }

    @SuppressWarnings("unchecked")
    private String getNickname(OAuth2User oAuth2User) {
        Map<String, String> properties = (Map<String, String>) oAuth2User.getAttributes().get("properties");
        return properties.get("nickname");
    }
}
