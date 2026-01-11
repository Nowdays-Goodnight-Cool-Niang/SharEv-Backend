package sharev.domain.gathering.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import sharev.domain.gathering.entity.IntroduceTemplate;

public interface IntroduceTemplateRepository extends JpaRepository<IntroduceTemplate, Long> {

    Optional<IntroduceTemplate> findByGatheringIdAndVersion(UUID gatheringId, int version);

    Optional<IntroduceTemplate> findTopByGatheringIdOrderByVersionDesc(UUID gatheringId);
}
