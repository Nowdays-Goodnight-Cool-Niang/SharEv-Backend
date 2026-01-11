package sharev.team.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sharev.account.entity.Account;
import sharev.member.entity.Member;
import sharev.member.entity.MemberRoleType;
import sharev.member.entity.MemberStatusType;
import sharev.member.repository.MemberRepository;
import sharev.team.dto.response.ResponseTeamInfoDto;
import sharev.team.entity.Team;
import sharev.team.exception.TeamNameDuplicateException;
import sharev.team.exception.TeamNotFoundException;
import sharev.team.repository.TeamRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void create(Account account, String title) {
        try {
            Team team = teamRepository.save(new Team(title));
            memberRepository.save(new Member(team, account, MemberStatusType.ACTIVATE, MemberRoleType.ADMIN));
        } catch (DataIntegrityViolationException e) {
            throw new TeamNameDuplicateException();
        }
    }

    public List<ResponseTeamInfoDto> getMyTeams(Account account) {
        return teamRepository.findMyTeams(account.getId());
    }

    public boolean isMember(Account account, Long teamId) {
        Optional<Team> optionalTeam = teamRepository.findById(teamId);

        if (optionalTeam.isEmpty()) {
            return false;
        }

        Team team = optionalTeam.get();

        return memberRepository.findByTeamAndAccount(team, account)
                .isPresent();
    }

    @Transactional
    public void updateTeamInfo(Long teamId, String title) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);

        team.updateTitle(title);
    }
}
