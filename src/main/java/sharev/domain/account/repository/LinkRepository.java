package sharev.domain.account.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sharev.domain.account.entity.Link;

public interface LinkRepository extends JpaRepository<Link, Long> {

    List<Link> findAllByAccountIdIn(Collection<Long> accountIds);
}
