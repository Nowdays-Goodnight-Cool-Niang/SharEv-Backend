package sharev.card_connection.repository;

import sharev.card_connection.entity.CardConnection;
import sharev.card_connection.entity.CardConnection.CardConnectionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardConnectionRepository extends JpaRepository<CardConnection, CardConnectionId>,
        CardConnectionRepositoryCustom {
}
