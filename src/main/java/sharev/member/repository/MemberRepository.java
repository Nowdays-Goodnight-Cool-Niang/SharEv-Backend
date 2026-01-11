package sharev.member.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sharev.account.entity.Account;
import sharev.member.entity.Member;
import sharev.team.entity.Team;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findAllByAccount(Account account);

    Optional<Member> findByTeamAndAccount(Team team, Account account);
}
