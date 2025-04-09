package org.example.backend.socialDex.repository;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.backend.socialDex.dto.response.QResponseSocialDexInfoDto_AccountInfo;
import org.example.backend.socialDex.dto.response.ResponseSocialDexInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.example.backend.account.entity.QAccount.account;
import static org.example.backend.socialDex.entity.QSocialDex.socialDex;

@RequiredArgsConstructor
public class SocialDexRepositoryImpl implements SocialDexRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Long getRegisterCount(UUID accountId, LocalDateTime snapshotTime) {
        return queryFactory
                .select(account.count())
                .from(account)
                .join(socialDex)
                .on(
                        socialDex.firstAccount.id.eq(account.id).and(socialDex.secondAccount.id.eq(accountId))
                                .or(
                                        socialDex.secondAccount.id.eq(account.id).and(socialDex.firstAccount.id.eq(accountId))
                                )
                )
                .where(account.id.ne(accountId), account.createdAt.loe(snapshotTime))
                .fetchOne();
    }

    public Page<ResponseSocialDexInfoDto.AccountInfo> findDexParticipants(UUID accountId, LocalDateTime snapshotTime, Pageable pageable) {

        List<ResponseSocialDexInfoDto.AccountInfo> content = queryFactory
                .select(new QResponseSocialDexInfoDto_AccountInfo(
                        account.name,
                        account.email,
                        account.linkedinUrl,
                        account.githubUrl,
                        account.instagramUrl,
                        account.teamName,
                        account.position,
                        account.introductionText,
                        new CaseBuilder()
                                .when(socialDex.id.isNotNull())
                                .then(true)
                                .otherwise(false)

                ))
                .from(account)
                .leftJoin(socialDex)
                .on(
                        socialDex.firstAccount.id.eq(account.id).and(socialDex.secondAccount.id.eq(accountId))
                                .or(
                                        socialDex.secondAccount.id.eq(account.id).and(socialDex.firstAccount.id.eq(accountId))
                                )
                )
                .where(account.id.ne(accountId))
                .orderBy(
                        new CaseBuilder()
                                .when(socialDex.id.isNotNull().and(socialDex.createdAt.loe(snapshotTime)))
                                .then(account.kakaoOauthId)
                                .otherwise(account.kakaoOauthId.negate())
                                .desc(),
                        account.createdAt.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(account.count())
                .from(account)
                .leftJoin(socialDex)
                .on(
                        socialDex.firstAccount.id.eq(account.id).and(socialDex.secondAccount.id.eq(accountId))
                                .or(
                                        socialDex.secondAccount.id.eq(account.id).and(socialDex.firstAccount.id.eq(accountId))
                                )
                )
                .where(account.id.ne(accountId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
