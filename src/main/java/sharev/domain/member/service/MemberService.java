package sharev.domain.member.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sharev.domain.account.entity.Account;
import sharev.domain.account.repository.AccountRepository;
import sharev.domain.member.dto.response.ResponseMemberDto;
import sharev.domain.member.entity.Member;
import sharev.domain.member.entity.MemberRoleType;
import sharev.domain.member.entity.MemberStatusType;
import sharev.domain.member.repository.MemberRepository;
import sharev.domain.team.entity.Team;
import sharev.domain.team.repository.TeamRepository;
import sharev.exception.CustomException;
import sharev.exception.ExceptionCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;

    public boolean isAdmin(Account account, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        Optional<Member> optionalMember = memberRepository.findByTeamAndAccount(team, account);

        if (optionalMember.isEmpty()) {
            throw new CustomException(ExceptionCode.TEAM_NOT_FOUND);
        }

        Member member = optionalMember.get();

        return member.getRole() == MemberRoleType.ADMIN;
    }

    public List<ResponseMemberDto> getMembers(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        return memberRepository.findAllByTeam(team).stream()
                .map(ResponseMemberDto::new)
                .toList();
    }

    @Transactional
    public void invite(Account account, Long teamId, String email) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        Account targetAccount = accountRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionCode.ACCOUNT_NOT_FOUND));

        memberRepository.findByTeamAndAccount(team, targetAccount)
                .ifPresent(m -> {
                    throw new CustomException(ExceptionCode.MEMBER_ALREADY_EXISTS);
                });

        memberRepository.save(new Member(team, targetAccount, MemberStatusType.INVITE, MemberRoleType.COMMON));
    }

    @Transactional
    public void acceptInvitation(Account account, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        Member member = memberRepository.findByTeamAndAccount(team, account)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        if (member.getStatus() != MemberStatusType.INVITE) {
            throw new CustomException(ExceptionCode.MEMBER_NOT_INVITED);
        }

        member.activate();
    }

    @Transactional
    public void leave(Account account, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        Member member = memberRepository.findByTeamAndAccount(team, account)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        validateNotLastAdmin(team, member);

        memberRepository.delete(member);
    }

    @Transactional
    public void updateRole(Long teamId, Long memberId, MemberRoleType role) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        Member member = findMemberInTeam(memberId, team);

        if (role != MemberRoleType.ADMIN) {
            validateNotLastAdmin(team, member);
        }

        member.updateRole(role);
    }

    @Transactional
    public void removeMember(Account account, Long teamId, Long memberId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        Member member = findMemberInTeam(memberId, team);

        if (member.getAccount().getId().equals(account.getId())) {
            throw new CustomException(ExceptionCode.CANNOT_REMOVE_SELF);
        }

        validateNotLastAdmin(team, member);

        memberRepository.delete(member);
    }

    private Member findMemberInTeam(Long memberId, Team team) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        if (!member.getTeam().getId().equals(team.getId())) {
            throw new CustomException(ExceptionCode.MEMBER_NOT_FOUND);
        }

        return member;
    }

    private void validateNotLastAdmin(Team team, Member member) {
        if (member.getRole() == MemberRoleType.ADMIN
                && memberRepository.countByTeamAndRole(team, MemberRoleType.ADMIN) <= 1) {
            throw new CustomException(ExceptionCode.CANNOT_REMOVE_LAST_ADMIN);
        }
    }
}
