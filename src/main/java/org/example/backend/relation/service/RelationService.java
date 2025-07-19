package org.example.backend.relation.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.backend.profile.dto.response.ProfileDto;
import org.example.backend.profile.dto.response.ResponseProfileDto;
import org.example.backend.profile.entity.Profile;
import org.example.backend.profile.repository.ProfileRepository;
import org.example.backend.relation.dto.response.ResponseRelationDto;
import org.example.backend.relation.dto.response.ResponseRelationProfileDto;
import org.example.backend.relation.entity.Relation;
import org.example.backend.relation.entity.Relation.RelationId;
import org.example.backend.relation.exception.SelfRelationException;
import org.example.backend.relation.repository.RelationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RelationService {

    private final RelationRepository relationRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public ResponseRelationDto register(UUID eventId, Long accountId, Integer targetPinNumber) {
        Profile profile = profileRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow();
        Profile targetProfile = profileRepository.findByEventIdAndPinNumber(eventId, targetPinNumber)
                .orElseThrow();

        if (profile.getId().equals(targetProfile.getId())) {
            throw new SelfRelationException();
        }

        Relation relation = new Relation(profile, targetProfile);

        try {
            relationRepository.save(relation);
        } catch (DataIntegrityViolationException e) {
            // ignore
        }

        RelationId relationId = relation.getId();
        return new ResponseRelationDto(relationId.getFirstProfileId(), relationId.getSecondProfileId());
    }

    public ResponseRelationProfileDto getParticipants(UUID eventId, Long accountId, LocalDateTime snapshotTime,
                                                      Pageable pageable) {
        Profile profile = profileRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow();

        Long registerCount = relationRepository.getRegisterCount(eventId, profile.getId(), snapshotTime);
        Page<ProfileDto> accountInfoPage =
                relationRepository.findRelationProfiles(eventId, profile.getId(), snapshotTime, pageable);
        Page<ResponseProfileDto> socialDexInfoPage = accountInfoPage.map(
                ProfileDto::convertResponseProfileDto);

        return new ResponseRelationProfileDto(registerCount, socialDexInfoPage);
    }
}
