package org.example.backend.socialDex.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.backend.socialDex.dto.response.QResponseSocialDexInfoDto_AccountInfo;
import org.example.backend.socialDex.dto.response.ResponseSocialDexInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.UUID;

import static org.example.backend.account.entity.QAccount.account;
import static org.example.backend.socialDex.entity.QSocialDex.socialDex;

@RequiredArgsConstructor
public class SocialDexRepositoryImpl implements SocialDexRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Page<ResponseSocialDexInfoDto.AccountInfo> findDexParticipants(UUID accountId, Pageable pageable) {

        List<ResponseSocialDexInfoDto.AccountInfo> content = queryFactory
                .select(new QResponseSocialDexInfoDto_AccountInfo(
                        account.email,
                        account.linkedinUrl,
                        account.githubUrl,
                        account.instagramUrl,
                        account.teamName,
                        account.position,
                        account.introductionText
                ))
                .from(account)
                .join(socialDex)
                .on(
                        socialDex.firstAccount.id.eq(account.id).and(socialDex.secondAccount.id.eq(accountId))
                                .or(
                                        socialDex.secondAccount.id.eq(account.id).and(socialDex.firstAccount.id.eq(accountId))
                                )
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(account.count())
                .from(account)
                .join(socialDex)
                .on(
                        socialDex.firstAccount.id.eq(account.id).and(socialDex.secondAccount.id.eq(accountId))
                                .or(
                                        socialDex.secondAccount.id.eq(account.id).and(socialDex.firstAccount.id.eq(accountId))
                                )
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
