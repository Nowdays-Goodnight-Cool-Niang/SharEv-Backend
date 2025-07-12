package org.example.backend.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.baseEntity.BaseTimeEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accounts")
public class Account extends BaseTimeEntity implements OAuth2User, Comparable<Account>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "account_id")
    private UUID id;

    @Column
    private Long kakaoOauthId;

    @Column
    @Setter
    private String name;

    @Column
    @Setter
    private String email;

    @Column
    @Setter
    private String linkedinUrl;

    @Column
    @Setter
    private String githubUrl;

    @Column
    @Setter
    private String instagramUrl;

    @Column
    @Setter
    private String teamName;

    @Column
    @Setter
    private String position;

    @Column
    @Setter
    private String introductionText;

    public Account(Long kakaoOauthId, String name) {
        this.kakaoOauthId = kakaoOauthId;
        this.name = name;
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

    @Override
    public int compareTo(Account o) {
        return this.id.compareTo(o.id);
    }
}
