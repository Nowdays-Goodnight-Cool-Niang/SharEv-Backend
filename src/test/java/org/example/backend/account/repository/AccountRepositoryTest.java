package org.example.backend.account.repository;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.example.backend.account.entity.Account;
import org.example.backend.config.JpaConfig;
import org.example.backend.config.QuerydslConfig;
import org.example.backend.event.entity.Event;
import org.example.backend.event.repository.EventRepository;
import org.example.backend.profile.entity.Profile;
import org.example.backend.profile.repository.ProfileRepository;
import org.example.backend.relation.entity.Relation;
import org.example.backend.relation.repository.RelationRepository;
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
    ProfileRepository profileRepository;

    @Autowired
    RelationRepository relationRepository;

    @Test
    @DisplayName("탈퇴 시 연계 삭제")
    void deleteCascade() throws Exception {

        // given
        Account kim = em.persist(new Account(1L, "김주호", "eora21@naver.com"));
        Account kwon = em.persist(new Account(2L, "권나연", "test1@test.com"));
        Account lee = em.persist(new Account(3L, "이유진", "test2@test.com"));

        Event event = em.persist(new Event());

        Profile jooho = em.persist(new Profile(event, kim, 1, 1));
        Profile nayeon = em.persist(new Profile(event, kwon, 2, 2));
        Profile yujin = em.persist(new Profile(event, lee, 3, 3));

        em.persist(new Relation(jooho, nayeon));
        em.persist(new Relation(jooho, yujin));
        em.persist(new Relation(nayeon, yujin));

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

        Assertions.assertThat(profileRepository.findAllByEventId(event.getId())).hasSize(2);

        Assertions.assertThat(relationRepository.getRegisterCount(event.getId(), jooho.getId(), LocalDateTime.now()))
                .isZero();
        Assertions.assertThat(relationRepository.getRegisterCount(event.getId(), nayeon.getId(), LocalDateTime.now()))
                .isEqualTo(1L);
        Assertions.assertThat(relationRepository.getRegisterCount(event.getId(), yujin.getId(), LocalDateTime.now()))
                .isEqualTo(1L);
    }
}
