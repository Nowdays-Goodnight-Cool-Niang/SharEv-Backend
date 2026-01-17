package sharev.domain.team.repository;

import static sharev.domain.member.entity.QMember.member;
import static sharev.domain.team.entity.QTeam.team;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import sharev.domain.member.entity.QMember;
import sharev.domain.team.dto.response.QResponseTeamInfoDto;
import sharev.domain.team.dto.response.ResponseTeamInfoDto;

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
}
