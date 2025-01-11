package org.example.backend.event_account.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.event.entity.Event;
import org.example.backend.event.repository.EventRepository;
import org.example.backend.event_account.dto.request.RequestPutParticipantInfo;
import org.example.backend.event_account.entity.EventAccount;
import org.example.backend.event_account.repository.EventAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventAccountService {
    private final EventAccountRepository eventAccountRepository;
    private final EventRepository eventRepository;

    @Transactional
    public void putParticipant(Account account, RequestPutParticipantInfo requestPutParticipantInfo) {
        Long eventId = requestPutParticipantInfo.eventId();

        EventAccount eventAccount = eventAccountRepository.findByEventIdAndAccountId(eventId, account.getId())
                .orElseGet(() -> createParticipant(eventId, account));

        eventAccount.setJobGroup(requestPutParticipantInfo.jobGroup());
        eventAccount.setTeamName(requestPutParticipantInfo.teamName());
        eventAccount.setProjectInfo(requestPutParticipantInfo.projectInfo());
    }

    private EventAccount createParticipant(Long eventId, Account account) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        return eventAccountRepository.save(new EventAccount(event, account));
    }
}
