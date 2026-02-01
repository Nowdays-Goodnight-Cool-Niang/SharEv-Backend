package sharev.domain.gathering.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import sharev.domain.gathering.entity.Gathering;
import sharev.domain.team.entity.Team;

public interface GatheringRepository extends JpaRepository<Gathering, UUID> {
    List<Gathering> findAllByTeam(Team team);
}
