package sharev.domain.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sharev.domain.team.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {
}
