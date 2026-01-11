package sharev.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sharev.team.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {
}
