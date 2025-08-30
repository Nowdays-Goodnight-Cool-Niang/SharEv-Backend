package sharev.card_connection.entity;

import jakarta.persistence.Column;
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
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sharev.base_entity.BaseTimeEntity;
import sharev.card.entity.Card;

@Entity
@Getter
@Table(name = "card_connections")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CardConnection extends BaseTimeEntity {

    @EmbeddedId
    private CardConnectionId id;

    @MapsId("myCardId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_card_id")
    private Card myCard;

    @MapsId("otherCardId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "other_card_id")
    private Card otherCard;

    @Column
    private boolean checkFlag;

    private CardConnection(Card myCard, Card otherCard, boolean checkFlag) {
        this.id = new CardConnectionId(myCard.getId(), otherCard.getId());
        this.myCard = myCard;
        this.otherCard = otherCard;
        this.checkFlag = checkFlag;
    }

    public static List<CardConnection> connect(Card myCard, Card otherCard) {
        return List.of(
                new CardConnection(myCard, otherCard, true),
                new CardConnection(otherCard, myCard, false)
        );
    }

    @Embeddable
    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class CardConnectionId implements Serializable {
        private Long myCardId;
        private Long otherCardId;
    }
}
