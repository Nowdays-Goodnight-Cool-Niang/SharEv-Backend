package org.example.backend.profile.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.base_entity.BaseTimeEntity;
import org.example.backend.event.entity.Event;
import org.example.backend.relation.entity.Relation;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profiles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "account_id"})
})
public class Profile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column
    private Integer pinNumber;

    @Column
    private Integer iconNumber;

    @Column
    private String introduce;

    @Column
    private String proudestExperience;

    @Column
    private String toughExperience;

    @OneToMany(mappedBy = "firstProfile", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<Relation> relationFirsts = new ArrayList<>();

    @OneToMany(mappedBy = "secondProfile", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<Relation> relationSeconds = new ArrayList<>();

    public Profile(Event event, Account account, Integer pinNumber, Integer iconNumber) {
        this.event = event;
        this.account = account;
        this.pinNumber = pinNumber;
        this.iconNumber = iconNumber;
    }

    public void updateInfo(String introduce, String proudestExperience, String toughExperience) {
        this.introduce = introduce;
        this.proudestExperience = proudestExperience;
        this.toughExperience = toughExperience;
    }
}
