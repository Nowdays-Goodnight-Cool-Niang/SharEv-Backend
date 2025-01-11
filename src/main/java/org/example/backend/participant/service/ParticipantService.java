package org.example.backend.participant.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.event.entity.Event;
import org.example.backend.event.repository.EventRepository;
import org.example.backend.participant.dto.request.RequestPutParticipantInfo;
import org.example.backend.participant.dto.response.ResponseGetParticipantInfo;
import org.example.backend.participant.entity.Participant;
import org.example.backend.participant.repository.FoundParticipantRepository;
import org.example.backend.participant.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final FoundParticipantRepository foundParticipantRepository;

    @Transactional
    public void putParticipant(Account account, RequestPutParticipantInfo requestPutParticipantInfo) {
        Long eventId = requestPutParticipantInfo.eventId();

        Participant participant = participantRepository.findByEventIdAndAccountId(eventId, account.getId())
                .orElseGet(() -> createParticipant(eventId, account));

        participant.setJobGroup(requestPutParticipantInfo.jobGroup());
        participant.setTeamName(requestPutParticipantInfo.teamName());
        participant.setProjectInfo(requestPutParticipantInfo.projectInfo());
    }

    private Participant createParticipant(Long eventId, Account account) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        return participantRepository.save(new Participant(event, account));
    }

    public ResponseGetParticipantInfo fetchParticipants(Long accountId, Long participantId) {
        validateFoundParticipant(accountId, participantId);
        return participantRepository.fetchDetailsBy(participantId);
    }

    private void validateFoundParticipant(Long accountId, Long participantId) {
        if (!foundParticipantRepository.existsByAccountIdAndParticipantId(accountId, participantId)) {
            throw new RuntimeException("Forbidden");
        }
    }
}
