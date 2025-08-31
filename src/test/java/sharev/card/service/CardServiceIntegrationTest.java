package sharev.card.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import sharev.account.entity.Account;
import sharev.account.repository.AccountRepository;
import sharev.card.entity.Card;
import sharev.card.exception.JoinAlreadyException;
import sharev.card.repository.CardRepository;
import sharev.event.entity.Event;
import sharev.event.repository.EventRepository;
import sharev.event.util.EventKeyGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CardServiceIntegrationTest {

    @Autowired
    private CardService cardService;

    @Autowired
    private CardRepository cardRepository;

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
        cardService.join(event.getId(), kim.getId());

        // then
        String eventKey = EventKeyGenerator.calculateEventPinKey(event.getId());

        assertThat(redisTemplate.hasKey(eventKey)).isTrue();
        Set<Object> pinNumbers = redisTemplate.opsForSet().members(eventKey);
        assertThat(pinNumbers).isNotEmpty();

        Card card = cardRepository.findByEventIdAndAccountId(event.getId(), kim.getId())
                .orElseThrow();
        assertThat(pinNumbers).doesNotContain(card.getPinNumber());
    }

    @DisplayName("중복하여 join 시 예외가 발생해야 한다")
    @Test
    void duplicateJoin() {

        // given
        Event event = eventRepository.save(new Event());
        Account kim = accountRepository.save(new Account(1L, "김주호", "eora21@naver.com"));

        // when
        // then
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> cardService.join(event.getId(), kim.getId()));
        org.junit.jupiter.api.Assertions.assertThrowsExactly(JoinAlreadyException.class,
                () -> cardService.join(event.getId(), kim.getId()));
    }
}
