package org.example.backend.socialDex.repository;

import org.example.backend.account.entity.Account;
import org.example.backend.config.QuerydslConfig;
import org.example.backend.socialDex.dto.response.ResponseSocialDexInfoDto;
import org.example.backend.socialDex.entity.SocialDex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import(QuerydslConfig.class)
class SocialDexRepositoryTest {

    @Autowired
    private SocialDexRepository socialDexRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("도감에 등록된 사용자 정보 조회")
    void findDexParticipants_ShouldReturnPageOfResponseSocialDexAccountInfoDto() {

        // Given
        Account firstAccount = new Account(1L, "1번 유저");
        firstAccount.setEmail("one@email.com");

        Account secondAccount = new Account(2L, "2번 유저");
        secondAccount.setEmail("two@email.com");

        Account thirdAccount = new Account(3L, "3번 유저");
        thirdAccount.setEmail("three@email.com");

        Account fourthAccount = new Account(4L, "4번 유저");
        fourthAccount.setEmail("four@email.com");

        Account fifthAccount = new Account(5L, "5번 유저");
        fifthAccount.setEmail("five@email.com");

        em.persist(firstAccount);
        em.persist(secondAccount);
        em.persist(thirdAccount);
        em.persist(fourthAccount);
        em.persist(fifthAccount);

        em.persist(new SocialDex(thirdAccount, fifthAccount));
        em.persist(new SocialDex(thirdAccount, fourthAccount));
        em.persist(new SocialDex(thirdAccount, firstAccount));

        em.persist(new SocialDex(secondAccount, firstAccount));
        em.persist(new SocialDex(secondAccount, fourthAccount));

        // When
        Page<ResponseSocialDexInfoDto.AccountInfo> result = socialDexRepository.findDexParticipants(thirdAccount.getId(), LocalDateTime.now(), PageRequest.of(0, 2));

        // Then
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(2, result.get().count());
        assertEquals(List.of("five@email.com", "four@email.com"), result.get().map(ResponseSocialDexInfoDto.AccountInfo::email).toList());
    }

    @Test
    @DisplayName("실시간 도감 반영")
    void afterRegistered() {

        // Given
        Account firstAccount = new Account(1L, "1번 유저");
        firstAccount.setEmail("one@email.com");

        Account secondAccount = new Account(2L, "2번 유저");
        secondAccount.setEmail("two@email.com");

        Account thirdAccount = new Account(3L, "3번 유저");
        thirdAccount.setEmail("three@email.com");

        Account fourthAccount = new Account(4L, "4번 유저");
        fourthAccount.setEmail("four@email.com");

        Account fifthAccount = new Account(5L, "5번 유저");
        fifthAccount.setEmail("five@email.com");

        em.persist(firstAccount);
        em.persist(secondAccount);
        em.persist(thirdAccount);
        em.persist(fourthAccount);
        em.persist(fifthAccount);

        // When
        em.persist(new SocialDex(thirdAccount, fifthAccount));
        LocalDateTime snapshotTime = LocalDateTime.now();
        Page<ResponseSocialDexInfoDto.AccountInfo> result = socialDexRepository.findDexParticipants(thirdAccount.getId(), snapshotTime, PageRequest.of(0, 2));

        // Then
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(2, result.get().count());
        assertEquals(List.of("five@email.com", "one@email.com"), result.get().map(ResponseSocialDexInfoDto.AccountInfo::email).toList());

        // When
        em.persist(new SocialDex(thirdAccount, fourthAccount));
        result = socialDexRepository.findDexParticipants(thirdAccount.getId(), snapshotTime, PageRequest.of(1, 2));

        // Then
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(2, result.get().count());
        assertEquals(List.of("two@email.com", "four@email.com"), result.get().map(ResponseSocialDexInfoDto.AccountInfo::email).toList());
        assertEquals(List.of(false, true), result.get().map(ResponseSocialDexInfoDto.AccountInfo::registerFlag).toList());
    }

    @Test
    @DisplayName("도감에 등록된 인원수 확인")
    void getRegisterCountTest() throws Exception {

        // given
        Account firstAccount = new Account(1L, "1번 유저");
        firstAccount.setEmail("one@email.com");

        Account secondAccount = new Account(2L, "2번 유저");
        secondAccount.setEmail("two@email.com");

        em.persist(firstAccount);
        em.persist(secondAccount);

        // when
        SocialDex socialDex = new SocialDex(firstAccount, secondAccount);
        em.persist(socialDex);

        // then
        assertEquals(0L, socialDexRepository.getRegisterCount(secondAccount.getId(), secondAccount.getCreatedAt()));
        assertEquals(1L, socialDexRepository.getRegisterCount(secondAccount.getId(), socialDex.getCreatedAt()));
    }
}
