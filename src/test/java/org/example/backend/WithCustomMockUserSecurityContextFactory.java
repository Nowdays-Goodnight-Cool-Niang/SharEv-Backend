package org.example.backend;

import java.lang.reflect.Field;
import java.util.List;
import org.example.backend.account.entity.Account;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        String role = annotation.role();

        Account account = new Account(1L, "test", "test@test.com");

        try {
            Class<Account> accountClass = Account.class;
            Field idField = accountClass.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(account, 1L);
        } catch (Exception e) {
            throw new RuntimeException("Account 리플렉션 중 문제 발생");
        }

        OAuth2AuthenticationToken token =
                new OAuth2AuthenticationToken(account, List.of(new SimpleGrantedAuthority(role)), "kakao");
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}
