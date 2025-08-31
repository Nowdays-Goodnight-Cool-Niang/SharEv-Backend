package sharev.card_connection.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import sharev.account.entity.Account;
import sharev.card.entity.Card;
import sharev.card.repository.CardRepository;
import sharev.card_connection.dto.response.ConnectedCardDto;
import sharev.card_connection.dto.response.ResponseConnectionInfoDto;
import sharev.card_connection.exception.RegisterMyselfException;
import sharev.card_connection.repository.CardConnectionRepository;
import sharev.event.entity.Event;
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
class CardConnectionServiceTest {

    @InjectMocks
    CardConnectionService cardConnectionService;

    @Mock
    CardConnectionRepository cardConnectionRepository;

    @Mock
    CardRepository cardRepository;

    @Test
    @DisplayName("자기 자신은 도감에 등록할 수 없다")
    void connectMyself() throws Exception {

        // given
        Event event = new Event();
        ReflectionTestUtils.setField(event, "id", UUID.randomUUID());

        Account account = new Account(1L, "테스트", "test@test.com");
        ReflectionTestUtils.setField(account, "id", 1L);

        Card card = new Card(event, account, 1, 1);
        ReflectionTestUtils.setField(card, "id", 1L);

        doReturn(Optional.of(card))
                .when(cardRepository).findByEventIdAndAccountId(event.getId(), account.getId());

        doReturn(Optional.of(card))
                .when(cardRepository).findByEventIdAndPinNumber(event.getId(), card.getPinNumber());

        // when
        // then
        Assertions.assertThrowsExactly(RegisterMyselfException.class, () ->
                cardConnectionService.connect(event.getId(), account.getId(), card.getPinNumber()));
    }

    @Test
    @DisplayName("도감에 등록된 사람과 등록되지 않은 사람은 데이터 획득에 차이가 있어야 한다")
    void relationProfile() throws Exception {

        // given
        Event event = new Event();
        ReflectionTestUtils.setField(event, "id", UUID.randomUUID());

        Account account = new Account(1L, "테스트", "test@test.com");
        ReflectionTestUtils.setField(account, "id", 1L);

        Card card = new Card(event, account, 1, 1);
        ReflectionTestUtils.setField(card, "id", 1L);

        doReturn(Optional.of(card)).when(cardRepository)
                .findByEventIdAndAccountId(any(UUID.class), anyLong());

        doReturn(1L).when(cardConnectionRepository)
                .getRegisterCount(any(UUID.class), anyLong(), any(LocalDateTime.class));

        Page<ConnectedCardDto> relationProfiles = new PageImpl<>(List.of(
                new ConnectedCardDto(2L, "김주호", "eora21@naver.com", null, null, null, 1, "자기소개", "뿌듯했던 경험", "힘들었던 경험",
                        true),
                new ConnectedCardDto(3L, "권나연", "kny@test.com", null, null, null, 2, "자기소개", "뿌듯했던 경험", "힘들었던 경험",
                        false),
                new ConnectedCardDto(4L, "이유진", "lyj@test.com", null, null, null, 3, "자기소개", "뿌듯했던 경험", "힘들었던 경험",
                        false)
        ));

        doReturn(relationProfiles).when(cardConnectionRepository)
                .findRelationProfiles(any(UUID.class), anyLong(), any(LocalDateTime.class), any(Pageable.class));

        // when
        ResponseConnectionInfoDto responseConnectionInfoDto = cardConnectionService.getConnectionInfos(event.getId(),
                account.getId(),
                LocalDateTime.now(), Pageable.ofSize(20));

        // then
        assertThat(responseConnectionInfoDto.registerCount()).isEqualTo(1);
        assertThat(responseConnectionInfoDto.relationProfiles().getContent()).hasSize(3)
                .extracting("name", "relationFlag")
                .containsExactlyInAnyOrder(
                        tuple("김주호", true),
                        tuple("권나연", false),
                        tuple("이유진", false)
                );
    }
}
