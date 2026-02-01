package sharev.domain.team.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sharev.base_entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "teams")
public class Team extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private CertificationType certification;

    @Column(unique = true)
    private String title;

    @Column
    private String content;

    @Column
    private Boolean activateFlag;

    public Team(String title) {
        this.title = title;
        this.certification = CertificationType.NONE;
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
