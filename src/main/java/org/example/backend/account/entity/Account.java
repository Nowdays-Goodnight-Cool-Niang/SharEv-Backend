package org.example.backend.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accounts")
public class Account implements OAuth2User {

    @Id
    @Column(name = "account_id")
    private Long id;

    @Column
    @Setter
    private String name;

    @Column
    @Setter
    private String phone;

    @Column
    @Setter
    private String githubUrl;

    @Column
    @Setter
    private String instagramUrl;

    @Column
    @Setter
    private String facebookUrl;

    @Column
    @Setter
    private Integer profileImageId;

    public Account(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getName() {
        return this.name;
    }
}
