package org.example.backend.account.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.base_entity.BaseTimeEntity;
import org.example.backend.profile.entity.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accounts")
public class Account extends BaseTimeEntity implements OAuth2User, Serializable {

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
    private boolean initialRoleGrantedFlag;

    @Column
    private String linkedinUrl;

    @Column
    private String githubUrl;

    @Column
    private String instagramUrl;

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<Profile> profiles = new ArrayList<>();

    public Account(Long kakaoOauthId, String name, String email) {
        this.kakaoOauthId = kakaoOauthId;
        this.name = name;
        this.email = email;
        this.initialRoleGrantedFlag = false;
    }

    public void updateInfo(String name, String email, String linkedinUrl, String githubUrl, String instagramUrl) {
        this.name = name;
        this.email = email;
        this.initialRoleGrantedFlag = true;
        this.linkedinUrl = linkedinUrl;
        this.githubUrl = githubUrl;
        this.instagramUrl = instagramUrl;
    }

    public boolean isAuthenticated() {
        return initialRoleGrantedFlag;
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
