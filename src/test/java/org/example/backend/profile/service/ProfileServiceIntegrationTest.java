package org.example.backend.profile.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.example.backend.account.entity.Account;
import org.example.backend.account.repository.AccountRepository;
import org.example.backend.event.entity.Event;
import org.example.backend.event.repository.EventRepository;
import org.example.backend.event.util.EventKeyGenerator;
import org.example.backend.profile.entity.Profile;
import org.example.backend.profile.exception.JoinAlreadyException;
import org.example.backend.profile.repository.ProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProfileServiceIntegrationTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @DisplayName("join 시 고유한 pin 번호가 생성되어야 한다.")
    @Test
    void generatesUniquePinNumberIfJoin() {

        // given
        Event event = eventRepository.save(new Event());
        Account kim = accountRepository.save(new Account(1L, "김주호", "eora21@naver.com"));

        // when
        profileService.join(event.getId(), kim.getId());

        // then
        String eventKey = EventKeyGenerator.calculateEventPinKey(event.getId());

        assertThat(redisTemplate.hasKey(eventKey)).isTrue();
        Set<Object> pinNumbers = redisTemplate.opsForSet().members(eventKey);
        assertThat(pinNumbers).isNotEmpty();

        Profile profile = profileRepository.findByEventIdAndAccountId(event.getId(), kim.getId())
                .orElseThrow();
        assertThat(pinNumbers).doesNotContain(profile.getPinNumber());
    }

    @DisplayName("중복하여 join 시 예외가 발생해야 한다")
    @Test
    void duplicateJoin() {

        // given
        Event event = eventRepository.save(new Event());
        Account kim = accountRepository.save(new Account(1L, "김주호", "eora21@naver.com"));

        // when
        // then
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> profileService.join(event.getId(), kim.getId()));
        org.junit.jupiter.api.Assertions.assertThrowsExactly(JoinAlreadyException.class,
                () -> profileService.join(event.getId(), kim.getId()));
    }
}
