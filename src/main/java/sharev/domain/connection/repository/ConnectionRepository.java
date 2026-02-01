package sharev.domain.connection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sharev.domain.connection.entity.Connection;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
}
