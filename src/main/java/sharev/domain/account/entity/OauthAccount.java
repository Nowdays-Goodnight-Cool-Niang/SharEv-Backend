package sharev.domain.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "oauth_accounts")
public class OauthAccount {

    @EmbeddedId
    private OauthAccountId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class OauthAccountId implements Serializable {

        @Enumerated(EnumType.STRING)
        @Column(name = "provider")
        private OauthProvider provider;

        @Column(name = "subject_identifier")
        private String subjectIdentifier;
    }

    public OauthAccount(OauthProvider oauthProvider, String subjectIdentifier, Account account) {
        this.id = new OauthAccountId(oauthProvider, subjectIdentifier);
        this.account = account;
    }
}
