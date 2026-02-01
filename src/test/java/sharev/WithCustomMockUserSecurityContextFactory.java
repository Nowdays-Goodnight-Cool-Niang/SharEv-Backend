package sharev;

import java.lang.reflect.Field;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;
import sharev.domain.account.entity.Account;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        String role = annotation.role();

        Account account = new Account("test", "test@test.com");
        ReflectionTestUtils.setField(account, "id", 1L);

        OAuth2AuthenticationToken token =
                new OAuth2AuthenticationToken(account, List.of(new SimpleGrantedAuthority(role)), "kakao");
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}
