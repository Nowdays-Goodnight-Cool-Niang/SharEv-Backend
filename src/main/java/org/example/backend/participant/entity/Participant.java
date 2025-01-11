package org.example.backend.participant.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.account.entity.Account;
import org.example.backend.event.entity.Event;

@Entity
@Getter
@Table(name = "participants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant {
    @Id
    @Column(name = "participant_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column
    @Setter
    private String jobGroup;

    @Column
    @Setter
    private String teamName;

    @Column
    @Setter
    private String projectInfo;

    public Participant(Event event, Account account, String jobGroup, String teamName, String projectInfo) {
        this.event = event;
        this.account = account;
        this.jobGroup = jobGroup;
        this.teamName = teamName;
        this.projectInfo = projectInfo;
    }
}
