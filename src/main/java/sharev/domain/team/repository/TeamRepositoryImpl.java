package sharev.domain.team.repository;

import static sharev.domain.account.entity.QAccount.account;
import static sharev.domain.member.entity.QMember.member;
import static sharev.domain.team.entity.QTeam.team;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import sharev.domain.member.entity.QMember;
import sharev.domain.team.dto.response.QResponseTeamInfoDto;
import sharev.domain.team.dto.response.QTempTeamMemberInfoDto;
import sharev.domain.team.dto.response.ResponseTeamInfoDto;
import sharev.domain.team.dto.response.TempTeamMemberInfoDto;

@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public List<ResponseTeamInfoDto> findMyTeams(Long accountId) {
        QMember teamMember = new QMember("teamMember");

        return queryFactory
                .select(new QResponseTeamInfoDto(
                        team.id,
                        team.title,
                        team.content,
                        team.createdAt,
                        member.role,
                        JPAExpressions
                                .select(teamMember.count().intValue())
                                .from(teamMember)
                                .where(teamMember.team.eq(team))
                ))
                .from(member)

                .join(team)
                .on(member.team.eq(team))

                .where(member.account.id.eq(accountId))
                .fetch();
    }

    public List<TempTeamMemberInfoDto> findMyTeamMembers(Long teamId) {
        return queryFactory
                .select(new QTempTeamMemberInfoDto(
                        account.name,
                        account.email
                ))
                .from(member)

                .leftJoin(account)
                .on(member.account.eq(account))

                .where(member.team.id.eq(teamId))
                .fetch();
    }
}
