package org.example.backend.socialDex.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.account.entity.Account;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialDex {

    @EmbeddedId
    SocialDexId id;

    @MapsId("firstAccountId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_account_id")
    Account firstAccount;

    @MapsId("secondAccountId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_account_id")
    Account secondAccount;

    public SocialDex(Account firstAccount, Account secondAccount) {
        Account[] accounts = new Account[]{firstAccount, secondAccount};
        Arrays.sort(accounts);

        this.id = new SocialDexId(accounts[0].getId(), accounts[1].getId());
        this.firstAccount = accounts[0];
        this.secondAccount = accounts[1];
    }

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    static public class SocialDexId implements Serializable {
        private UUID firstAccountId;
        private UUID secondAccountId;
    }
}
