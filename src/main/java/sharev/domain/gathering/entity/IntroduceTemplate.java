package sharev.domain.gathering.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import sharev.base_entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "introduce_templates")
public class IntroduceTemplate extends BaseTimeEntity {

    @Id
    @Column(name = "introduce_template_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    private Gathering gathering;

    @Column
    private Integer version;

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    private IntroduceTemplateContent content;

    public IntroduceTemplate(Gathering gathering, Integer templateVersion,
                             IntroduceTemplateContent content) {
        this.gathering = gathering;
        this.version = templateVersion;
        this.content = content;
    }

    public boolean validateIntroduce(int version, Map<String, String> introduce) {
        if (this.version != version) {
            return false;
        }

        Set<String> introduceFields = introduce.keySet();

        Set<String> introduceTemplateFields = this.content.getFields();

        if (introduceTemplateFields.size() != introduceFields.size()) {
            return false;
        }

        HashSet<String> subtract = new HashSet<>(introduceFields);
        subtract.removeAll(introduceFields);

        return subtract.isEmpty();
    }

    public static IntroduceTemplate updateContent(IntroduceTemplate introduceTemplate,
                                                  IntroduceTemplateContent introduceTemplateContent) {

        IntroduceTemplateContent originTemplateContent = introduceTemplate.content;

        if (originTemplateContent.hasSameFields(introduceTemplateContent.fieldPlaceholders())) {
            introduceTemplate.content = introduceTemplateContent;
            return introduceTemplate;
        }

        return new IntroduceTemplate(introduceTemplate.gathering, introduceTemplate.version + 1,
                introduceTemplateContent);
    }
}
