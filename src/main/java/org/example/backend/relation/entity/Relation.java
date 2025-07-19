package org.example.backend.relation.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.base_entity.BaseTimeEntity;
import org.example.backend.profile.entity.Profile;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "relations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Relation extends BaseTimeEntity {

    @EmbeddedId
    private RelationId id;

    @MapsId("firstProfileId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_profile_id")
    private Profile firstProfile;

    @MapsId("secondProfileId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_profile_id")
    private Profile secondProfile;

    public Relation(Profile firstProfile, Profile secondProfile) {
        this.id = new RelationId(firstProfile.getId(), secondProfile.getId());

        if (this.id.firstProfileId.equals(firstProfile.getId())) {
            this.firstProfile = firstProfile;
            this.secondProfile = secondProfile;
        } else {
            this.firstProfile = secondProfile;
            this.secondProfile = firstProfile;
        }
    }

    @Embeddable
    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RelationId implements Serializable {
        private Long firstProfileId;
        private Long secondProfileId;

        public RelationId(Long firstProfileId, Long secondProfileId) {
            Long[] profileIds = {firstProfileId, secondProfileId};

            Arrays.sort(profileIds);

            this.firstProfileId = profileIds[0];
            this.secondProfileId = profileIds[1];
        }
    }
}
