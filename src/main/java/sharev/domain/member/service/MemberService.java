package sharev.domain.member.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sharev.domain.account.entity.Account;
import sharev.domain.member.entity.Member;
import sharev.domain.member.entity.MemberRoleType;
import sharev.domain.member.repository.MemberRepository;
import sharev.domain.team.entity.Team;
import sharev.domain.team.exception.TeamNotFoundException;
import sharev.domain.team.repository.TeamRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    public boolean isAdmin(Account account, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);

        Optional<Member> optionalMember = memberRepository.findByTeamAndAccount(team, account);

        if (optionalMember.isEmpty()) {
            throw new TeamNotFoundException();
        }

        Member member = optionalMember.get();

        return member.getRole() == MemberRoleType.ADMIN;
    }
}
