package sharev.domain.card.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import sharev.base_entity.BaseTimeEntity;
import sharev.domain.account.entity.Account;
import sharev.domain.card.exception.InvalidIntroduceTemplateException;
import sharev.domain.gathering.entity.Gathering;
import sharev.domain.gathering.entity.IntroduceTemplate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cards")
public class Card extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    private Gathering gathering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column
    private Integer pinNumber;

    @Column
    private Integer templateVersion;

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> introductionText;

    public Card(Gathering gathering, Account account, Integer pinNumber) {
        this.gathering = gathering;
        this.account = account;
        this.pinNumber = pinNumber;
    }

    public void updateIntroductionText(IntroduceTemplate introduceTemplate, Integer templateVersion,
                                       Map<String, String> introductionText) {

        if (!introduceTemplate.validateIntroduce(templateVersion, introductionText)) {
            throw new InvalidIntroduceTemplateException();
        }

        this.templateVersion = templateVersion;
        this.introductionText = introductionText;
    }

    public boolean isCompleted() {
        return Objects.nonNull(this.pinNumber) && Objects.nonNull(this.introductionText);
    }
}
