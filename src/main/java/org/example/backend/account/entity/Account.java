package org.example.backend.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.base_entity.BaseTimeEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accounts")
public class Account extends BaseTimeEntity implements OAuth2User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column
    private Long kakaoOauthId;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String linkedinUrl;

    @Column
    private String githubUrl;

    @Column
    private String instagramUrl;

    public Account(Long kakaoOauthId, String name) {
        this.kakaoOauthId = kakaoOauthId;
        this.name = name;
    }

    public void updateInfo(String name, String email, String linkedinUrl, String githubUrl, String instagramUrl) {
        this.name = name;
        this.email = email;
        this.linkedinUrl = linkedinUrl;
        this.githubUrl = githubUrl;
        this.instagramUrl = instagramUrl;
    }

    public boolean isAuthenticated() {
        return this.name != null && this.email != null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (this.isAuthenticated()) {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return List.of();
    }

    @Override
    public String getName() {
        return this.name;
    }
}
