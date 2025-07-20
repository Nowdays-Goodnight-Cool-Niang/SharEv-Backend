package org.example.backend.account.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.account.dto.request.RequestDeleteDto;
import org.example.backend.account.dto.request.RequestUpdateInfoDto;
import org.example.backend.account.dto.response.ResponseAccountInfo;
import org.example.backend.account.entity.Account;
import org.example.backend.account.service.AccountService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @PatchMapping
    public ResponseEntity<Void> updateAccountInfo(@AuthenticationPrincipal Account account,
                                                  @Valid @RequestBody RequestUpdateInfoDto requestUpdateInfoDto,
                                                  HttpSession session) {

        Account newAccount = accountService.updateAccountInfo(account.getId(), requestUpdateInfoDto.name(),
                requestUpdateInfoDto.email(), requestUpdateInfoDto.linkedinUrl(), requestUpdateInfoDto.githubUrl(),
                requestUpdateInfoDto.instagramUrl());

        updateSessionInfo(newAccount, session);

        return ResponseEntity.ok().build();
    }

    private static void updateSessionInfo(Account newAccount, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String clientRegistrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        Authentication newAuth = new OAuth2AuthenticationToken(newAccount, newAccount.getAuthorities(),
                clientRegistrationId);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(newAuth);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }

    @GetMapping
    public ResponseEntity<ResponseAccountInfo> getAccountInfo(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(new ResponseAccountInfo(account));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Account account,
                                       @RequestBody RequestDeleteDto requestDeleteDto, HttpSession session) {
        accountService.delete(account);
        accountService.saveFeedback(requestDeleteDto.feedback());

        session.invalidate();

        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();

        disconnectOauth();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    private void disconnectOauth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken oauth2Authentication) {
            String clientRegistrationId = oauth2Authentication.getAuthorizedClientRegistrationId();

            OAuth2AuthorizedClient authorizedClient = authorizedClientService
                    .loadAuthorizedClient(clientRegistrationId, authentication.getName());

            if (authorizedClient != null) {
                OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                String tokenValue = accessToken.getTokenValue();

                requestDisconnectOauth(tokenValue);
            }
        }
    }

    private static void requestDisconnectOauth(String tokenValue) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenValue);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

        restTemplate.exchange("https://kapi.kakao.com/v1/user/unlink", HttpMethod.POST, entity, String.class);
    }
}
