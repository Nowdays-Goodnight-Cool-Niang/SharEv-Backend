package org.example.backend.participant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.base_entity.BaseTimeEntity;
import org.example.backend.event.entity.Event;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "participants")
public class Participant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
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
    private String reminderExperience;

    @Column
    private String wantAgainExperience;

    public Participant(Event event, Account account, Integer pinNumber, Integer iconNumber) {
        this.event = event;
        this.account = account;
        this.pinNumber = pinNumber;
        this.iconNumber = iconNumber;
    }

    public void updateInfo(String introduce, String reminderExperience, String wantAgainExperience) {
        this.introduce = introduce;
        this.reminderExperience = reminderExperience;
        this.wantAgainExperience = wantAgainExperience;
    }
}
