package sharev.domain.account.service;

import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sharev.domain.account.entity.Account;
import sharev.domain.account.entity.OauthAccount;
import sharev.domain.account.entity.OauthAccount.OauthAccountId;
import sharev.domain.account.entity.OauthProvider;
import sharev.domain.account.repository.AccountRepository;
import sharev.domain.account.repository.OauthAccountRepository;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
    private final OauthAccountRepository oauthAccountRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
        Map<String, Object> kakaoUserInfo = oAuth2User.getAttribute("kakao_account");

        if (Objects.isNull(kakaoUserInfo)) {
            throw new OAuth2AuthenticationException("로그인 도중 오류가 발생했습니다. 운영진에게 문의해주세요.");
        }

        Long kakaoOauthId = getKakaoOauthId(oAuth2User);
        String name = getNickname(kakaoUserInfo);
        String email = getEmail(kakaoUserInfo);

        OauthAccount oauthAccount = oauthAccountRepository.findById(
                        new OauthAccountId(OauthProvider.KAKAO, kakaoOauthId.toString()))
                .orElseGet(() -> {
                    Account account = accountRepository.save(new Account(name, email));
                    return oauthAccountRepository.save(
                            new OauthAccount(OauthProvider.KAKAO, kakaoOauthId.toString(), account)
                    );
                });

        return accountRepository.findById(oauthAccount.getAccount().getId())
                .orElseThrow(() -> new OAuth2AuthenticationException("계정을 찾을 수 없습니다."));
    }

    @SuppressWarnings("unchecked")
    private Long getKakaoOauthId(OAuth2User oAuth2User) {
        Long kakaoOauthId = oAuth2User.getAttribute("id");

        if (kakaoOauthId == null) {
            throw new OAuth2AuthenticationException("로그인 도중 오류가 발생했습니다. 운영진에게 문의해주세요.");
        }

        return kakaoOauthId;
    }

    @SuppressWarnings("unchecked")
    private String getNickname(Map<String, Object> kakaoUserInfo) {
        Map<String, String> attributes = (Map<String, String>) kakaoUserInfo.get("profile");
        String nickname = attributes.get("nickname");

        if (Objects.isNull(nickname) || nickname.isBlank()) {
            return "";
        }

        return nickname;
    }

    @SuppressWarnings("unchecked")
    private String getEmail(Map<String, Object> kakaoUserInfo) {
        String email = (String) kakaoUserInfo.get("email");

        if (Objects.isNull(email) || email.isBlank()) {
            return "";
        }

        return email;
    }
}
