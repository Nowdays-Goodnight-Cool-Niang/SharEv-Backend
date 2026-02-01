package sharev.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sharev.domain.account.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
