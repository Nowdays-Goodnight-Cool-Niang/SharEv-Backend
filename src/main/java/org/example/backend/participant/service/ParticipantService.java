package org.example.backend.participant.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.account.repository.AccountRepository;
import org.example.backend.event.entity.Event;
import org.example.backend.event.repository.EventRepository;
import org.example.backend.participant.dto.response.ParticipantProfile;
import org.example.backend.participant.dto.response.ParticipantProfileDto;
import org.example.backend.participant.dto.response.ResponseParticipantInfoDto;
import org.example.backend.participant.entity.Participant;
import org.example.backend.participant.repository.ParticipantRepository;
import org.example.backend.social_dex.entity.SocialDex.SocialDexId;
import org.example.backend.social_dex.repository.SocialDexRepository;
import org.example.backend.util.RandomNumberCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantService {
    private static final int START_RANDOM_NUMBER = 1;
    private static final int END_RANDOM_NUMBER = 12;

    private final ParticipantRepository participantRepository;
    private final AccountRepository accountRepository;
    private final SocialDexRepository socialDexRepository;
    private final EventRepository eventRepository;
    private final RandomNumberCalculator randomNumberCalculator;

    public void join(UUID eventId, Long accountId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow();
        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        // TODO: pinNumber는 redis 통해 계산할 것(검증 -> redisson으로 분산락, 이중 체킹)

        participantRepository.save(new Participant(event, account, randomNumberCalculator.getRandom(1000, 9999),
                randomNumberCalculator.getRandom(START_RANDOM_NUMBER, END_RANDOM_NUMBER)));
    }

    @Transactional
    public void updateInfo(UUID eventId, Long accountId, String introduce, String reminderExperience,
                           String wantAgainExperience) {
        Participant participant = participantRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow();

        participant.updateInfo(introduce, reminderExperience, wantAgainExperience);
    }

    public ResponseParticipantInfoDto getParticipantInfo(UUID eventId, Long accountId) {
        Participant participant = participantRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow();

        return new ResponseParticipantInfoDto(participant.getIntroduce(), participant.getReminderExperience(),
                participant.getWantAgainExperience());
    }

    public ParticipantProfileDto getParticipantProfile(UUID eventId, Long accountId, Integer pinNumber) {
        Participant participant = participantRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow();

        Participant targetParticipant = participantRepository.findByEventIdAndPinNumber(eventId, pinNumber)
                .orElseThrow();

        boolean registerFlag = socialDexRepository.existsById(
                new SocialDexId(participant.getId(), targetParticipant.getId()));

        return new ParticipantProfile(targetParticipant, registerFlag);
    }
}
