//package org.example.backend.relation.repository;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import org.example.backend.account.entity.Account;
//import org.example.backend.config.JpaConfig;
//import org.example.backend.config.QuerydslConfig;
//import org.example.backend.event.entity.Event;
//import org.example.backend.profile.entity.Profile;
//import org.example.backend.relation.dto.response.RelationProfileDto;
//import org.example.backend.relation.entity.Relation;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//
//@DataJpaTest
//@Import({QuerydslConfig.class, JpaConfig.class})
//class RelationRepositoryTest {
//
//    @Autowired
//    private RelationRepository relationRepository;
//
//    @Autowired
//    private TestEntityManager em;
//
//    @Test
//    @DisplayName("도감에 등록된 사용자 정보 조회")
//    void findDexParticipants_ShouldReturnPageOfResponseSocialDexAccountInfoDto() {
//
//        // Given
//        Account firstAccount = em.persist(new Account(1L, "1번 유저", "one@email.com"));
//        Account secondAccount = em.persist(new Account(2L, "2번 유저", "two@email.com"));
//        Account thirdAccount = em.persist(new Account(3L, "3번 유저", "three@email.com"));
//        Account fourthAccount = em.persist(new Account(4L, "4번 유저", "four@email.com"));
//        Account fifthAccount = em.persist(new Account(5L, "5번 유저", "five@email.com"));
//
//        Event event = em.persist(new Event());
//
//        Profile firstProfile = em.persist(new Profile(event, firstAccount, 1000, 1));
//        Profile secondProfile = em.persist(new Profile(event, secondAccount, 2000, 2));
//        Profile thirdProfile = em.persist(new Profile(event, thirdAccount, 3000, 3));
//        Profile fourthProfile = em.persist(new Profile(event, fourthAccount, 4000, 4));
//        Profile fifthProfile = em.persist(new Profile(event, fifthAccount, 5000, 5));
//
//        em.persist(new Relation(thirdProfile, fifthProfile));
//        em.persist(new Relation(thirdProfile, fourthProfile));
//        em.persist(new Relation(thirdProfile, firstProfile));
//
//        em.persist(new Relation(secondProfile, firstProfile));
//        Relation relation = em.persist(new Relation(secondProfile, fourthProfile));
//
//        // When
//        Page<RelationProfileDto> result = relationRepository.findRelationProfiles(event.getId(),
//                thirdProfile.getId(), relation.getCreatedAt(), PageRequest.of(0, 2));
//
//        // Then
//        assertNotNull(result);
//        assertEquals(4, result.getTotalElements());
//        assertEquals(2, result.getTotalPages());
//        assertEquals(2, result.get().count());
//        assertEquals(List.of("five@email.com", "four@email.com"),
//                result.get().map(RelationProfileDto::email).toList());
//    }
//
//    @Test
//    @DisplayName("실시간 도감 반영")
//    void afterRegistered() {
//
//        // Given
//        Account firstAccount = em.persist(new Account(1L, "1번 유저", "one@email.com"));
//        Account secondAccount = em.persist(new Account(2L, "2번 유저", "two@email.com"));
//        Account thirdAccount = em.persist(new Account(3L, "3번 유저", "three@email.com"));
//        Account fourthAccount = em.persist(new Account(4L, "4번 유저", "four@email.com"));
//        Account fifthAccount = em.persist(new Account(5L, "5번 유저", "five@email.com"));
//
//        Event event = em.persist(new Event());
//
//        em.persist(new Profile(event, firstAccount, 1000, 1));
//        em.persist(new Profile(event, secondAccount, 2000, 2));
//        Profile thirdProfile = em.persist(new Profile(event, thirdAccount, 3000, 3));
//        Profile fourthProfile = em.persist(new Profile(event, fourthAccount, 4000, 4));
//        Profile fifthProfile = em.persist(new Profile(event, fifthAccount, 5000, 5));
//
//        // When
//        Relation relation = em.persist(new Relation(thirdProfile, fifthProfile));
//        LocalDateTime snapshotTime = relation.getCreatedAt();
//        Page<RelationProfileDto> result = relationRepository.findRelationProfiles(event.getId(),
//                thirdProfile.getId(), snapshotTime, PageRequest.of(0, 2));
//
//        // Then
//        assertNotNull(result);
//        assertEquals(4, result.getTotalElements());
//        assertEquals(2, result.getTotalPages());
//        assertEquals(2, result.get().count());
//        assertEquals(List.of("five@email.com", "one@email.com"),
//                result.get().map(RelationProfileDto::email).toList());
//
//        // When
//        em.persist(new Relation(thirdProfile, fourthProfile));
//        result = relationRepository.findRelationProfiles(event.getId(), thirdProfile.getId(), snapshotTime,
//                PageRequest.of(1, 2));
//
//        // Then
//        assertNotNull(result);
//        assertEquals(4, result.getTotalElements());
//        assertEquals(2, result.getTotalPages());
//        assertEquals(2, result.get().count());
//        assertEquals(List.of("two@email.com", "four@email.com"),
//                result.get().map(RelationProfileDto::email).toList());
//        assertEquals(List.of(false, true),
//                result.get().map(RelationProfileDto::relationFlag).toList());
//    }
//
//    @Test
//    @DisplayName("도감에 등록된 인원수 확인")
//    void getRegisterCountTest() throws Exception {
//
//        // given
//        Account firstAccount = em.persist(new Account(1L, "1번 유저", "one@email.com"));
//        Account secondAccount = em.persist(new Account(2L, "2번 유저", "two@email.com"));
//
//        Event event = em.persist(new Event());
//
//        Profile firstProfile = em.persist(new Profile(event, firstAccount, 1000, 1));
//        Profile secondProfile = em.persist(new Profile(event, secondAccount, 2000, 2));
//
//        // when
//        Relation relation = new Relation(firstProfile, secondProfile);
//        em.persistAndFlush(relation);
//
//        // then
//        assertEquals(0L, relationRepository.getRegisterCount(event.getId(), secondProfile.getId(),
//                secondProfile.getCreatedAt()));
//        assertEquals(1L,
//                relationRepository.getRegisterCount(event.getId(), secondProfile.getId(), LocalDateTime.now()));
//    }
//}
