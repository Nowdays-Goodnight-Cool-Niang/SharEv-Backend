package sharev.card_connection.repository;

import static sharev.account.entity.QAccount.account;
import static sharev.card.entity.QCard.card;
import static sharev.card_connection.entity.QCardConnection.cardConnection;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import sharev.card_connection.dto.response.ConnectedCardDto;
import sharev.card_connection.dto.response.QConnectedCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class CardConnectionRepositoryImpl implements CardConnectionRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Long getRegisterCount(UUID eventId, Long cardId, LocalDateTime snapshotTime) {
        return queryFactory
                .select(card.count())
                .from(card)
                .join(cardConnection)
                .on(
                        cardConnection.myCard.id.eq(cardId)
                                .and(cardConnection.otherCard.id.eq(card.id))
                )
                .where(
                        card.event.id.eq(eventId),
                        cardConnection.createdAt.loe(snapshotTime)
                )
                .fetchOne();
    }

    public Page<ConnectedCardDto> findRelationProfiles(UUID eventId, Long cardId,
                                                       LocalDateTime snapshotTime,
                                                       Pageable pageable) {

        List<ConnectedCardDto> content = queryFactory
                .select(new QConnectedCardDto(
                        account.id,
                        account.name,
                        account.email,
                        account.linkedinUrl,
                        account.githubUrl,
                        account.instagramUrl,
                        card.iconNumber,
                        card.introduce,
                        card.proudestExperience,
                        card.toughExperience,
                        new CaseBuilder()
                                .when(cardConnection.id.isNotNull())
                                .then(true)
                                .otherwise(false)

                ))
                .from(card)

                .leftJoin(cardConnection)
                .on(
                        cardConnection.myCard.id.eq(cardId)
                                .and(cardConnection.otherCard.id.eq(card.id))
                )

                .leftJoin(account)
                .on(card.account.eq(account))

                .where(card.event.id.eq(eventId))
                .orderBy(
                        // TODO: 정렬 기준 개선 필요
                        new CaseBuilder()
                                .when(cardConnection.id.isNotNull().and(cardConnection.createdAt.loe(snapshotTime)))
                                .then(account.id)
                                .otherwise(account.id.negate())
                                .desc(),
                        account.createdAt.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(card.count())
                .from(card)
                .where(card.event.id.eq(eventId), card.id.ne(cardId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
