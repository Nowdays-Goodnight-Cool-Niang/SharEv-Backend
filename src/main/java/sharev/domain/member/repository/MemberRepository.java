package sharev.domain.member.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import sharev.domain.account.entity.Account;
import sharev.domain.member.entity.Member;
import sharev.domain.member.entity.MemberRoleType;
import sharev.domain.team.entity.Team;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findAllByAccount(Account account);

    Optional<Member> findByTeamAndAccount(Team team, Account account);

    @EntityGraph(attributePaths = "account")
    List<Member> findAllByTeam(Team team);

    Optional<Member> findByTeamAndAccountEmail(Team team, String email);

    long countByTeamAndRole(Team team, MemberRoleType role);
}
