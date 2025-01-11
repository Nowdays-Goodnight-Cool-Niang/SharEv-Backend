package org.example.backend.participant.repository;

import java.util.Optional;

import org.example.backend.participant.dto.response.ResponseGetParticipantInfo;
import org.example.backend.participant.entity.QParticipant;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParticipantRepositoryImpl implements ParticipantRepositoryExtension {

    private final JPAQueryFactory queryFactory;

    @Override
    public ResponseGetParticipantInfo fetchDetailsBy(Long id) {
        return Optional.ofNullable(
            queryFactory
                .select(Projections.constructor(
                    ResponseGetParticipantInfo.class,
                    QParticipant.participant.id.as("id"),
                    QParticipant.participant.account.name.as("name"),
                    QParticipant.participant.account.profileImageId.as("profileImageId"),
                    QParticipant.participant.account.phone.as("phone"),
                    QParticipant.participant.jobGroup.as("jobGroup"),
                    QParticipant.participant.teamName.as("teamName"),
                    QParticipant.participant.projectInfo.as("projectInfo")
                ))
                .from(QParticipant.participant)
                .where(
                    QParticipant.participant.id.eq(id)
                )
                .fetchOne()
        ).orElseThrow(() -> new RuntimeException("Does not found participant"));
    }
}
