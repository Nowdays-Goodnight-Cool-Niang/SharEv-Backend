package org.example.backend.relation.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.backend.profile.entity.Profile;
import org.example.backend.profile.exception.ProfileNotFoundException;
import org.example.backend.profile.repository.ProfileRepository;
import org.example.backend.relation.dto.response.RelationProfileDto;
import org.example.backend.relation.dto.response.ResponseRelationInfoDto;
import org.example.backend.relation.dto.response.ResponseRelationProfileDto;
import org.example.backend.relation.entity.Relation;
import org.example.backend.relation.exception.RegisterAlreadyException;
import org.example.backend.relation.exception.RegisterMyselfException;
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
    public void register(UUID eventId, Long accountId, Integer targetPinNumber) {
        Profile profile = profileRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow(ProfileNotFoundException::new);
        Profile targetProfile = profileRepository.findByEventIdAndPinNumber(eventId, targetPinNumber)
                .orElseThrow(ProfileNotFoundException::new);

        if (profile.getId().equals(targetProfile.getId())) {
            throw new RegisterMyselfException();
        }

        Relation relation = new Relation(profile, targetProfile);

        try {
            relationRepository.saveAndFlush(relation);
        } catch (DataIntegrityViolationException e) {
            throw new RegisterAlreadyException();
        }
    }

    public ResponseRelationInfoDto getParticipants(UUID eventId, Long accountId, LocalDateTime snapshotTime,
                                                   Pageable pageable) {
        Profile profile = profileRepository.findByEventIdAndAccountId(eventId, accountId)
                .orElseThrow(ProfileNotFoundException::new);

        Long registerCount = relationRepository.getRegisterCount(eventId, profile.getId(), snapshotTime);
        Page<RelationProfileDto> relationProfiles =
                relationRepository.findRelationProfiles(eventId, profile.getId(), snapshotTime, pageable);
        Page<ResponseRelationProfileDto> responseRelationProfiles =
                relationProfiles.map(RelationProfileDto::convertIfNonRelation);

        return new ResponseRelationInfoDto(registerCount, responseRelationProfiles);
    }
}
