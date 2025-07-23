package org.example.backend.jmeter;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jmeter")
public class JmeterController {

    private final JmeterService jmeterService;

    @PostMapping("/signup")
    public ResponseEntity<JmeterSignupResponse> signup(@RequestBody JmeterSignupRequest jmeterSignupRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jmeterService.signup(jmeterSignupRequest.kakaoId()));
    }

    @PostMapping("/login")
    public ResponseEntity<JmeterLoginResponse> login(@RequestBody JmeterLoginRequest jmeterLoginRequest,
                                                     HttpSession session) {

        Account account = jmeterService.login(jmeterLoginRequest.kakaoId());

        Authentication authentication = new OAuth2AuthenticationToken(
                account,
                account.getAuthorities(),
                "kakao"
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return ResponseEntity.ok(new JmeterLoginResponse(account.getKakaoOauthId()));
    }
}
