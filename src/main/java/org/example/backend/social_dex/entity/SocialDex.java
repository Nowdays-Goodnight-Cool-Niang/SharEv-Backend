package org.example.backend.social_dex.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import java.io.Serializable;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.base_entity.BaseTimeEntity;
import org.example.backend.participant.entity.Participant;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class SocialDex extends BaseTimeEntity {

    @EmbeddedId
    private SocialDexId id;

    @MapsId("firstParticipantId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "first_participant_id")
    private Participant firstParticipant;

    @MapsId("secondParticipantId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "second_participant_id")
    private Participant secondParticipant;

    public SocialDex(Participant firstParticipant, Participant secondParticipant) {
        Participant[] participants = new Participant[]{firstParticipant, secondParticipant};
        Arrays.sort(participants);

        this.id = new SocialDexId(participants[0].getId(), participants[1].getId());
        this.firstParticipant = participants[0];
        this.secondParticipant = participants[1];
    }

    @Embeddable
    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class SocialDexId implements Serializable {
        private Long firstParticipantId;
        private Long secondParticipantId;
    }
}
