package sharev.connection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sharev.connection.entity.Connection;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
}
