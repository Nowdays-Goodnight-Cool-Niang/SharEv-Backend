package sharev.team.repository;

import static sharev.domain.account.entity.QAccount.account;
import static sharev.domain.member.entity.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import sharev.domain.team.dto.response.QResponseTeamInfoDto;
import sharev.team.dto.response.ResponseTeamInfoDto;

@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public List<ResponseTeamInfoDto> findMyTeams(Long accountId) {
        return queryFactory
                .select(new QResponseTeamInfoDto(
                        member.team.id,
                        member.team.title,
                        member.team.content,
                        member.team.createdAt,
                        member.count().intValue()
                ))
                .from(account)
                .where(account.id.eq(accountId))

                .leftJoin(member)
                .on(account.eq(member.account))

                .fetch();
    }
}
