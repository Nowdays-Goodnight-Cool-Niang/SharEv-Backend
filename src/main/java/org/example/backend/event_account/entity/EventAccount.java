package org.example.backend.event_account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.account.entity.Account;
import org.example.backend.event.entity.Event;

@Entity
@Getter
@Table(name = "event_account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventAccount {
    @Id
    @Column(name = "event_account_id")
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

    public EventAccount(Event event, Account account) {
        this.event = event;
        this.account = account;
    }
}
