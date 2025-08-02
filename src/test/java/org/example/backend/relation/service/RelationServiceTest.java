package org.example.backend.relation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.backend.account.entity.Account;
import org.example.backend.event.entity.Event;
import org.example.backend.profile.entity.Profile;
import org.example.backend.profile.repository.ProfileRepository;
import org.example.backend.relation.dto.response.RelationProfileDto;
import org.example.backend.relation.dto.response.ResponseRelationInfoDto;
import org.example.backend.relation.exception.RegisterMyselfException;
import org.example.backend.relation.repository.RelationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RelationServiceTest {

    @InjectMocks
    RelationService relationService;

    @Mock
    RelationRepository relationRepository;

    @Mock
    ProfileRepository profileRepository;

    @Test
    @DisplayName("자기 자신은 도감에 등록할 수 없다")
    void registerMyself() throws Exception {

        // given
        Event event = new Event();
        ReflectionTestUtils.setField(event, "id", UUID.randomUUID());

        Account account = new Account(1L, "테스트", "test@test.com");
        ReflectionTestUtils.setField(account, "id", 1L);

        Profile profile = new Profile(event, account, 1, 1);
        ReflectionTestUtils.setField(profile, "id", 1L);

        doReturn(Optional.of(profile))
                .when(profileRepository).findByEventIdAndAccountId(event.getId(), account.getId());

        doReturn(Optional.of(profile))
                .when(profileRepository).findByEventIdAndPinNumber(event.getId(), profile.getPinNumber());

        // when
        // then
        Assertions.assertThrowsExactly(RegisterMyselfException.class, () ->
                relationService.register(event.getId(), account.getId(), profile.getPinNumber()));
    }

    @Test
    @DisplayName("도감에 등록된 사람과 등록되지 않은 사람은 데이터 획득에 차이가 있어야 한다")
    void relationProfile() throws Exception {

        // given
        Event event = new Event();
        ReflectionTestUtils.setField(event, "id", UUID.randomUUID());

        Account account = new Account(1L, "테스트", "test@test.com");
        ReflectionTestUtils.setField(account, "id", 1L);

        Profile profile = new Profile(event, account, 1, 1);
        ReflectionTestUtils.setField(profile, "id", 1L);

        doReturn(Optional.of(profile)).when(profileRepository)
                .findByEventIdAndAccountId(any(UUID.class), anyLong());

        doReturn(1L).when(relationRepository)
                .getRegisterCount(any(UUID.class), anyLong(), any(LocalDateTime.class));

        Page<RelationProfileDto> relationProfiles = new PageImpl<>(List.of(
                new RelationProfileDto(2L, "김주호", "eora21@naver.com", null, null, null, 1, "자기소개", "뿌듯했던 경험", "힘들었던 경험",
                        true),
                new RelationProfileDto(3L, "권나연", "kny@test.com", null, null, null, 2, "자기소개", "뿌듯했던 경험", "힘들었던 경험",
                        false),
                new RelationProfileDto(4L, "이유진", "lyj@test.com", null, null, null, 3, "자기소개", "뿌듯했던 경험", "힘들었던 경험",
                        false)
        ));

        doReturn(relationProfiles).when(relationRepository)
                .findRelationProfiles(any(UUID.class), anyLong(), any(LocalDateTime.class), any(Pageable.class));

        // when
        ResponseRelationInfoDto responseRelationInfoDto = relationService.getParticipants(event.getId(),
                account.getId(),
                LocalDateTime.now(), Pageable.ofSize(20));

        // then
        assertThat(responseRelationInfoDto.registerCount()).isEqualTo(1);
        assertThat(responseRelationInfoDto.relationProfiles().getContent()).hasSize(3)
                .extracting("name", "relationFlag")
                .containsExactlyInAnyOrder(
                        tuple("김주호", true),
                        tuple("권나연", false),
                        tuple("이유진", false)
                );
    }
}
