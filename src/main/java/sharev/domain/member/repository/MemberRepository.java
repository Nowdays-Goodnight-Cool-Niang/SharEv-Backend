package sharev.domain.member.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sharev.domain.account.entity.Account;
import sharev.domain.member.entity.Member;
import sharev.domain.team.entity.Team;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findAllByAccount(Account account);

    Optional<Member> findByTeamAndAccount(Team team, Account account);
}
