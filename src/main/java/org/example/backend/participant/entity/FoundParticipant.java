package org.example.backend.participant.entity;

import java.io.Serializable;

import org.example.backend.account.entity.Account;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "found_participants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoundParticipant {
    @EmbeddedId
    private Pk pk;

    @MapsId("participantId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @MapsId("accountId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Embeddable
    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pk implements Serializable {
        private Long participantId;
        private Long accountId;
    }

    public FoundParticipant(Pk pk, Participant participant, Account account) {
        this.pk = pk;
        this.participant = participant;
        this.account = account;
    }
}
