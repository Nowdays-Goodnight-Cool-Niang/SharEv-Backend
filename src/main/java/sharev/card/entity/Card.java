package sharev.card.entity;

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
import org.springframework.util.StringUtils;
import sharev.account.entity.Account;
import sharev.base_entity.BaseTimeEntity;
import sharev.card_connection.entity.CardConnection;
import sharev.event.entity.Event;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cards", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "account_id"})
})
public class Card extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "myCard", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<CardConnection> myCardConnections = new ArrayList<>();

    @OneToMany(mappedBy = "otherCard", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<CardConnection> otherCardConnections = new ArrayList<>();

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

    public Card(Event event, Account account, Integer pinNumber, Integer iconNumber) {
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

    public boolean isCompleted() {
        return StringUtils.hasText(introduce) &&
                StringUtils.hasText(proudestExperience) &&
                StringUtils.hasText(toughExperience);
    }
}
