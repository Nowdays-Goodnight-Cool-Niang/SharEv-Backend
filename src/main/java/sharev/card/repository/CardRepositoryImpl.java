package sharev.card.repository;

import static sharev.domain.account.entity.QAccount.account;
import static sharev.domain.card.entity.QCard.card;
import static sharev.domain.connection.entity.QConnection.connection;
import static sharev.domain.gathering.entity.QIntroduceTemplate.introduceTemplate;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import sharev.card.dto.response.TempResponseCardDto;
import sharev.domain.card.dto.response.QTempResponseCardDto;

@RequiredArgsConstructor
public class CardRepositoryImpl implements CardRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Long searchConnectionCount(UUID gatheringId, Long cardId, LocalDateTime snapshotTime) {
        return queryFactory
                .select(card.count())
                .from(card)
                .join(connection)
                .on(
                        connection.myCard.id.eq(cardId)
                                .and(connection.otherCard.id.eq(card.id))
                )
                .where(
                        card.gathering.id.eq(gatheringId),
                        connection.createdAt.loe(snapshotTime)
                )
                .fetchOne();
    }

    @Override
    public Page<TempResponseCardDto> searchTempCards(UUID gatheringId, Long accountId, LocalDateTime snapshotTime,
                                                     Pageable pageable) {

        BooleanExpression isConnected = connection.id.isNotNull()
                .and(connection.createdAt.loe(snapshotTime));

        List<TempResponseCardDto> content = queryFactory
                .select(new QTempResponseCardDto(
                        new CaseBuilder()
                                .when(connection.id.isNotNull())
                                .then(true)
                                .otherwise(false),
                        card,
                        account,
                        introduceTemplate
                ))
                .from(card)

                .join(account)
                .on(card.account.id.eq(account.id))

                .join(introduceTemplate)
                .on(card.templateVersion.eq(introduceTemplate.version))

                .leftJoin(connection)
                .on(connection.otherCard.id.eq(card.id))

                .where(card.gathering.id.eq(gatheringId))
                .orderBy(
                        new CaseBuilder()
                                .when(isConnected).then(1)
                                .otherwise(2)
                                .asc(),

                        new CaseBuilder()
                                .when(isConnected).then(connection.createdAt)
                                .otherwise(card.createdAt)
                                .desc()
                )

                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(card.count())
                .from(card)
                .where(card.gathering.id.eq(gatheringId), card.account.id.ne(accountId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
