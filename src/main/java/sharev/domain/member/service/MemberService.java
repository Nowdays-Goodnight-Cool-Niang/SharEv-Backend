package sharev.domain.member.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sharev.domain.account.entity.Account;
import sharev.domain.account.exception.AccountNotFoundException;
import sharev.domain.account.repository.AccountRepository;
import sharev.domain.member.dto.response.ResponseMemberDto;
import sharev.domain.member.entity.Member;
import sharev.domain.member.entity.MemberRoleType;
import sharev.domain.member.entity.MemberStatusType;
import sharev.domain.member.exception.CannotRemoveLastAdminException;
import sharev.domain.member.exception.CannotRemoveSelfException;
import sharev.domain.member.exception.MemberAlreadyExistsException;
import sharev.domain.member.exception.MemberNotFoundException;
import sharev.domain.member.exception.MemberNotInvitedException;
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
    private final AccountRepository accountRepository;

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

    public List<ResponseMemberDto> getMembers(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);

        return memberRepository.findAllByTeam(team).stream()
                .map(ResponseMemberDto::new)
                .toList();
    }

    @Transactional
    public void invite(Account account, Long teamId, String email) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);

        Account targetAccount = accountRepository.findByEmail(email)
                .orElseThrow(AccountNotFoundException::new);

        memberRepository.findByTeamAndAccount(team, targetAccount)
                .ifPresent(m -> {
                    throw new MemberAlreadyExistsException();
                });

        memberRepository.save(new Member(team, targetAccount, MemberStatusType.INVITE, MemberRoleType.COMMON));
    }

    @Transactional
    public void acceptInvitation(Account account, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);

        Member member = memberRepository.findByTeamAndAccount(team, account)
                .orElseThrow(MemberNotFoundException::new);

        if (member.getStatus() != MemberStatusType.INVITE) {
            throw new MemberNotInvitedException();
        }

        member.activate();
    }

    @Transactional
    public void leave(Account account, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);

        Member member = memberRepository.findByTeamAndAccount(team, account)
                .orElseThrow(MemberNotFoundException::new);

        validateNotLastAdmin(team, member);

        memberRepository.delete(member);
    }

    @Transactional
    public void updateRole(Long teamId, Long memberId, MemberRoleType role) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);

        Member member = findMemberInTeam(memberId, team);

        if (role != MemberRoleType.ADMIN) {
            validateNotLastAdmin(team, member);
        }

        member.updateRole(role);
    }

    @Transactional
    public void removeMember(Account account, Long teamId, Long memberId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(TeamNotFoundException::new);

        Member member = findMemberInTeam(memberId, team);

        if (member.getAccount().getId().equals(account.getId())) {
            throw new CannotRemoveSelfException();
        }

        validateNotLastAdmin(team, member);

        memberRepository.delete(member);
    }

    private Member findMemberInTeam(Long memberId, Team team) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        if (!member.getTeam().getId().equals(team.getId())) {
            throw new MemberNotFoundException();
        }

        return member;
    }

    private void validateNotLastAdmin(Team team, Member member) {
        if (member.getRole() == MemberRoleType.ADMIN
                && memberRepository.countByTeamAndRole(team, MemberRoleType.ADMIN) <= 1) {
            throw new CannotRemoveLastAdminException();
        }
    }
}
