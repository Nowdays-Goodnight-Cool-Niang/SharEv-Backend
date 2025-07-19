package org.example.backend.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.csrf(AbstractHttpConfigurer::disable);

        http.formLogin(AbstractHttpConfigurer::disable);

        http.cors(withDefaults());

        http.oauth2Login(httpSecurityOAuth2LoginConfigurer ->
                httpSecurityOAuth2LoginConfigurer.successHandler(loginSuccessHandler()));

        http.logout(logoutConfig -> logoutConfig.logoutSuccessHandler(logoutSuccessHandler()));

        http.exceptionHandling(exceptionHandleConfig -> {
            exceptionHandleConfig.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
            exceptionHandleConfig.accessDeniedHandler((request, response, accessDeniedException) ->
                    response.sendError(HttpServletResponse.SC_NOT_FOUND));
        });

        http.authorizeHttpRequests(authorizeRequests -> {
            authorizeRequests.requestMatchers(HttpMethod.POST, "/signup", "/login")
                    .permitAll();
            authorizeRequests.requestMatchers("/accounts")
                    .authenticated();
            authorizeRequests.requestMatchers(HttpMethod.GET)
                    .permitAll();
            authorizeRequests.anyRequest()
                    .hasRole("USER");
        });

        return http.build();
    }

    @Bean
    @Profile("dev")
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin 설정
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(List.of("*"));

        // 허용할 헤더 설정
        configuration.setAllowedHeaders(List.of("*"));

        // 인증 정보(쿠키 등) 허용
        configuration.setAllowCredentials(true);

        // 경로 매핑
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    private AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            Account account = (Account) authentication.getPrincipal();

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> responseData = accountEntityToLoginResponse(account);

            String jsonResponse = objectMapper.writeValueAsString(responseData);
            response.getWriter().write(jsonResponse);
        };
    }

    private static Map<String, Object> accountEntityToLoginResponse(Account account) {
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("isAuthenticated", account.isAuthenticated());
        responseData.put("id", account.getId());
        responseData.put("name", account.getName());
        responseData.put("email", account.getEmail());
        responseData.put("linkedinUrl", account.getLinkedinUrl());
        responseData.put("githubUrl", account.getGithubUrl());
        responseData.put("instagramUrl", account.getInstagramUrl());
        return responseData;
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            response.setStatus(HttpServletResponse.SC_OK);
        };
    }
}
