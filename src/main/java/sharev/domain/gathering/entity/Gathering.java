package sharev.domain.gathering.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.UuidGenerator.Style;
import sharev.base_entity.BaseTimeEntity;
import sharev.domain.team.entity.Team;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "gatherings")
@SQLRestriction("deleted_at IS NULL")
public class Gathering extends BaseTimeEntity {

    @Id
    @UuidGenerator(style = Style.VERSION_7)
    @Column(name = "gathering_id")
    private UUID id;

    @Column(columnDefinition = "visible_type")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private GatheringVisibleType visible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column
    String title;

    @Column
    String content;

    @Column
    private LocalDateTime startAt;

    @Column
    private LocalDateTime endAt;

    @Column
    private String place;

    @Column
    private String imageUrl;

    @Column
    private String gatheringUrl;

    @Column
    private String contact;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private LocalDateTime registerStartAt;

    @Column
    private LocalDateTime registerEndAt;

    public Gathering(Team team, GatheringVisibleType visible, String title, String content,
                     LocalDateTime startAt, LocalDateTime endAt, String place,
                     String imageUrl, String gatheringUrl, String contact,
                     LocalDateTime registerStartAt, LocalDateTime registerEndAt) {
        this.team = team;
        this.visible = visible;
        this.title = title;
        this.content = content;
        this.startAt = startAt;
        this.endAt = endAt;
        this.place = place;
        this.imageUrl = imageUrl;
        this.gatheringUrl = gatheringUrl;
        this.contact = contact;
        this.registerStartAt = registerStartAt;
        this.registerEndAt = registerEndAt;
    }

    public void update(GatheringVisibleType visible, String title, String content,
                       LocalDateTime startAt, LocalDateTime endAt, String place,
                       String imageUrl, String gatheringUrl, String contact,
                       LocalDateTime registerStartAt, LocalDateTime registerEndAt) {
        this.visible = visible;
        this.title = title;
        this.content = content;
        this.startAt = startAt;
        this.endAt = endAt;
        this.place = place;
        this.imageUrl = imageUrl;
        this.gatheringUrl = gatheringUrl;
        this.contact = contact;
        this.registerStartAt = registerStartAt;
        this.registerEndAt = registerEndAt;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
