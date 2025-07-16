package org.example.backend.social_dex.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.backend.participant.entity.Participant;
import org.example.backend.participant.repository.ParticipantRepository;
import org.example.backend.social_dex.dto.response.ResponseSocialDexDto;
import org.example.backend.social_dex.dto.response.ResponseSocialDexInfoDto;
import org.example.backend.social_dex.dto.response.ResponseSocialDexInfoDto.ParticipantInfo;
import org.example.backend.social_dex.entity.SocialDex;
import org.example.backend.social_dex.repository.SocialDexRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialDexService {

    private final SocialDexRepository socialDexRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public ResponseSocialDexDto updateSocialDex(UUID eventId, Long accountId, Integer targetPinNumber) {
        Participant participant = participantRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow();
        Participant targetParticipant = participantRepository.findByEventIdAndPinNumber(eventId, targetPinNumber)
                .orElseThrow();

        SocialDex socialDex = new SocialDex(participant, targetParticipant);
        socialDexRepository.insertIgnore(socialDex);

        SocialDex.SocialDexId socialDexId = socialDex.getId();
        return new ResponseSocialDexDto(socialDexId.getFirstParticipantId(), socialDexId.getSecondParticipantId());
    }

    public ResponseSocialDexInfoDto getSocialDex(UUID eventId, Long accountId, LocalDateTime snapshotTime,
                                                 Pageable pageable) {
        Participant participant = participantRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow();

        Long registerCount = socialDexRepository.getRegisterCount(eventId, participant.getId(), snapshotTime);
        Page<ParticipantInfo> accountInfoPage =
                socialDexRepository.findDexParticipants(eventId, participant.getId(), snapshotTime, pageable);
        Page<ResponseSocialDexInfoDto.SocialDexInfo> socialDexInfoPage = accountInfoPage.map(
                ParticipantInfo::convertSocialDexInfo);

        return new ResponseSocialDexInfoDto(registerCount, socialDexInfoPage);
    }
}
