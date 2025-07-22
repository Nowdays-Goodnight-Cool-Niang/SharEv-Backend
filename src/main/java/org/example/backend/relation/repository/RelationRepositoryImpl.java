package org.example.backend.relation.repository;

import static org.example.backend.account.entity.QAccount.account;
import static org.example.backend.profile.entity.QProfile.profile;
import static org.example.backend.relation.entity.QRelation.relation;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.backend.relation.dto.response.QRelationProfileDto;
import org.example.backend.relation.dto.response.RelationProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class RelationRepositoryImpl implements RelationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Long getRegisterCount(UUID eventId, Long profileId, LocalDateTime snapshotTime) {
        return queryFactory
                .select(profile.count())
                .from(profile)
                .join(relation)
                .on(
                        relation.firstProfile.id.eq(profile.id)
                                .and(relation.secondProfile.id.eq(profileId))
                                .or(
                                        relation.secondProfile.id.eq(profile.id)
                                                .and(relation.firstProfile.id.eq(profileId))
                                )
                )
                .where(
                        profile.event.id.eq(eventId),
                        profile.id.ne(profileId),
                        relation.createdAt.loe(snapshotTime)
                )
                .fetchOne();
    }

    public Page<RelationProfileDto> findRelationProfiles(UUID eventId, Long profileId,
                                                         LocalDateTime snapshotTime,
                                                         Pageable pageable) {

        List<RelationProfileDto> content = queryFactory
                .select(new QRelationProfileDto(
                        account.id,
                        account.name,
                        account.email,
                        account.linkedinUrl,
                        account.githubUrl,
                        account.instagramUrl,
                        profile.iconNumber,
                        profile.introduce,
                        profile.proudestExperience,
                        profile.toughExperience,
                        new CaseBuilder()
                                .when(relation.id.isNotNull())
                                .then(true)
                                .otherwise(false)

                ))
                .from(profile)

                .leftJoin(relation)
                .on(
                        relation.firstProfile.id.eq(profile.id)
                                .and(relation.secondProfile.id.eq(profileId))
                                .or(
                                        relation.secondProfile.id.eq(profile.id)
                                                .and(relation.firstProfile.id.eq(profileId))
                                )
                )

                .leftJoin(account)
                .on(profile.account.eq(account))

                .where(profile.event.id.eq(eventId), profile.id.ne(profileId))
                .orderBy(
                        new CaseBuilder()
                                .when(relation.id.isNotNull().and(relation.createdAt.loe(snapshotTime)))
                                .then(account.id)
                                .otherwise(account.id.negate())
                                .desc(),
                        account.createdAt.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(profile.count())
                .from(profile)
                .where(profile.event.id.eq(eventId), profile.id.ne(profileId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
