package sharev.domain.connection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sharev.base_entity.BaseTimeEntity;
import sharev.domain.card.entity.Card;

@Entity
@Getter
@Table(name = "connections")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Connection extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connection_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_card_id")
    private Card myCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "other_card_id")
    private Card otherCard;

    @Column
    @Enumerated(EnumType.STRING)
    private ConnectionStatusType status;

    @Column
    private String memo;

    private Connection(Card myCard, Card otherCard) {
        this.myCard = myCard;
        this.otherCard = otherCard;
        this.status = ConnectionStatusType.REGISTRATION;
    }

    public static List<Connection> connect(Card myCard, Card otherCard) {
        return List.of(new Connection(myCard, otherCard), new Connection(otherCard, myCard));
    }
}
