package org.example.backend.event.repository;

import java.util.List;
import java.util.Optional;

import org.example.backend.event.dto.response.EventDetailsDto;
import org.example.backend.event.dto.response.EventOverviewDto;
import org.example.backend.event.dto.response.EventParticipantOverviewDto;
import org.example.backend.event.entity.QEvent;
import org.example.backend.participant.entity.QFoundParticipant;
import org.example.backend.participant.entity.QParticipant;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryExtension {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<EventOverviewDto> fetchList() {
        return queryFactory
            .select(Projections.constructor(
                EventOverviewDto.class,
                QEvent.event.id.as("id"),
                QEvent.event.title.as("title"),
                QEvent.event.imageUrl.as("imageUrl"),
                QEvent.event.content.as("content"),
                QEvent.event.startedAt.as("startedAt"),
                QEvent.event.endedAt.as("endedAt")
            ))
            .from(QEvent.event)
            .fetch();
    }

    @Override
    public EventDetailsDto fetchBy(Long eventId, Long accountId) {
        EventDetailsDto dto = Optional.ofNullable(
            queryFactory
                .select(Projections.constructor(
                    EventDetailsDto.class,
                    QEvent.event.id.as("id"),
                    QEvent.event.title.as("title"),
                    QEvent.event.imageUrl.as("imageUrl"),
                    QEvent.event.content.as("content"),
                    QEvent.event.organizer.as("organizer"),
                    QEvent.event.place.as("place"),
                    QEvent.event.eventUrl.as("eventUrl"),
                    QEvent.event.startedAt.as("startedAt"),
                    QEvent.event.endedAt.as("endedAt"),
                    QParticipant.participant.account.id.as("accountId")
                ))
                .from(QEvent.event)
                .leftJoin(QParticipant.participant)
                .on(
                    QParticipant.participant.account.id.eq(accountId)
                )
                .where(
                    QEvent.event.id.eq(eventId)
                )
                .fetchOne()
        ).orElseThrow(() ->  new RuntimeException("Does not found event"));

        Long count = queryFactory
            .select(QParticipant.participant.count())
            .from(QParticipant.participant)
            .where(
                QParticipant.participant.event.id.eq(eventId)
            )
            .fetchOne();

        dto.setTotalRegistration(count);
        return dto;
    }

    @Override
    public List<EventParticipantOverviewDto> fetchParticipantList(Long eventId, Long accountId) {
        return queryFactory
            .select(Projections.constructor(
                EventParticipantOverviewDto.class,
                QParticipant.participant.id.as("id"),
                QParticipant.participant.account.name.as("name"),
                QParticipant.participant.account.profileImageId.as("profileImageId"),
                QFoundParticipant.foundParticipant.account.id.as("accountId")
            ))
            .from(QParticipant.participant)
            .leftJoin(QFoundParticipant.foundParticipant)
            .on(
                QFoundParticipant.foundParticipant.account.id.eq(accountId)
            )
            .where(
                QParticipant.participant.event.id.eq(eventId)
            )
            .fetch();
    }

}
