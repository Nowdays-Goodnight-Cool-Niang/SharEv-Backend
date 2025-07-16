package org.example.backend.social_dex.repository;

import static org.example.backend.account.entity.QAccount.account;
import static org.example.backend.participant.entity.QParticipant.participant;
import static org.example.backend.social_dex.entity.QSocialDex.socialDex;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.backend.social_dex.dto.response.QResponseSocialDexInfoDto_ParticipantInfo;
import org.example.backend.social_dex.dto.response.ResponseSocialDexInfoDto.ParticipantInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class SocialDexRepositoryImpl implements SocialDexRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Long getRegisterCount(UUID eventId, Long participantId, LocalDateTime snapshotTime) {
        return queryFactory
                .select(participant.count())
                .from(participant)
                .join(socialDex)
                .on(
                        socialDex.firstParticipant.id.eq(participant.id)
                                .and(socialDex.secondParticipant.id.eq(participantId))
                                .or(
                                        socialDex.secondParticipant.id.eq(participant.id)
                                                .and(socialDex.firstParticipant.id.eq(participantId))
                                )
                )
                .where(
                        participant.event.id.eq(eventId),
                        participant.id.ne(participantId),
                        socialDex.createdAt.loe(snapshotTime)
                )
                .fetchOne();
    }

    public Page<ParticipantInfo> findDexParticipants(UUID eventId, Long participantId,
                                                     LocalDateTime snapshotTime,
                                                     Pageable pageable) {

        List<ParticipantInfo> content = queryFactory
                .select(new QResponseSocialDexInfoDto_ParticipantInfo(
                        account.id,
                        account.name,
                        account.email,
                        account.linkedinUrl,
                        account.githubUrl,
                        account.instagramUrl,
                        participant.introduce,
                        participant.reminderExperience,
                        participant.wantAgainExperience,
                        new CaseBuilder()
                                .when(socialDex.id.isNotNull())
                                .then(true)
                                .otherwise(false)

                ))
                .from(participant)

                .leftJoin(socialDex)
                .on(
                        socialDex.firstParticipant.id.eq(participant.id)
                                .and(socialDex.secondParticipant.id.eq(participantId))
                                .or(
                                        socialDex.secondParticipant.id.eq(participant.id)
                                                .and(socialDex.firstParticipant.id.eq(participantId))
                                )
                )

                .leftJoin(account)
                .on(participant.account.eq(account))

                .where(participant.event.id.eq(eventId), participant.id.ne(participantId))
                .orderBy(
                        new CaseBuilder()
                                .when(socialDex.id.isNotNull().and(socialDex.createdAt.loe(snapshotTime)))
                                .then(account.id)
                                .otherwise(account.id.negate())
                                .desc(),
                        account.createdAt.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(participant.count())
                .from(participant)
                .where(participant.event.id.eq(eventId), participant.id.ne(participantId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
