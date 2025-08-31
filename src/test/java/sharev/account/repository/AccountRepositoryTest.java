package sharev.account.repository;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import sharev.account.entity.Account;
import sharev.card.entity.Card;
import sharev.card.repository.CardRepository;
import sharev.card_connection.entity.CardConnection;
import sharev.card_connection.repository.CardConnectionRepository;
import sharev.config.JpaConfig;
import sharev.config.QuerydslConfig;
import sharev.event.entity.Event;
import sharev.event.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({QuerydslConfig.class, JpaConfig.class})
class AccountRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardConnectionRepository cardConnectionRepository;

    @Test
    @DisplayName("탈퇴 시 연계 삭제")
    void deleteCascade() throws Exception {

        // given
        Account kim = em.persist(new Account(1L, "김주호", "eora21@naver.com"));
        Account kwon = em.persist(new Account(2L, "권나연", "test1@test.com"));
        Account lee = em.persist(new Account(3L, "이유진", "test2@test.com"));

        Event event = em.persist(new Event());

        Card jooho = em.persist(new Card(event, kim, 1, 1));
        Card nayeon = em.persist(new Card(event, kwon, 2, 2));
        Card yujin = em.persist(new Card(event, lee, 3, 3));

        CardConnection.connect(jooho, nayeon)
                .forEach(em::persist);
        CardConnection.connect(jooho, yujin)
                .forEach(em::persist);
        CardConnection.connect(nayeon, yujin)
                .forEach(em::persist);

        em.flush();
        em.clear();

        // when
        accountRepository.delete(accountRepository.findByKakaoOauthId(1L).get());
        accountRepository.flush();

        // then
        Assertions.assertThat(accountRepository.findByKakaoOauthId(1L)).isEmpty();
        Assertions.assertThat(accountRepository.findByKakaoOauthId(2L)).isPresent();
        Assertions.assertThat(accountRepository.findByKakaoOauthId(3L)).isPresent();

        Assertions.assertThat(eventRepository.findById(event.getId())).isPresent();

        Assertions.assertThat(cardRepository.findAllByEventId(event.getId())).hasSize(2);

        Assertions.assertThat(
                        cardConnectionRepository.getRegisterCount(event.getId(), jooho.getId(), LocalDateTime.now()))
                .isZero();
        Assertions.assertThat(
                        cardConnectionRepository.getRegisterCount(event.getId(), nayeon.getId(), LocalDateTime.now()))
                .isEqualTo(1L);
        Assertions.assertThat(
                        cardConnectionRepository.getRegisterCount(event.getId(), yujin.getId(), LocalDateTime.now()))
                .isEqualTo(1L);
    }
}
