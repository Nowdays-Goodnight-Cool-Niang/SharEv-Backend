package org.example.backend.socialDex.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.account.entity.Account;
import org.example.backend.baseEntity.BaseTimeEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class SocialDex extends BaseTimeEntity {

    @EmbeddedId
    private SocialDexId id;

    @MapsId("firstAccountId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "first_account_id")
    private Account firstAccount;

    @MapsId("secondAccountId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "second_account_id")
    private Account secondAccount;

    public SocialDex(Account firstAccount, Account secondAccount) {
        Account[] accounts = new Account[]{firstAccount, secondAccount};
        Arrays.sort(accounts);

        this.id = new SocialDexId(accounts[0].getId(), accounts[1].getId());
        this.firstAccount = accounts[0];
        this.secondAccount = accounts[1];
    }

    @Embeddable
    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    static public class SocialDexId implements Serializable {
        private UUID firstAccountId;
        private UUID secondAccountId;
    }
}
