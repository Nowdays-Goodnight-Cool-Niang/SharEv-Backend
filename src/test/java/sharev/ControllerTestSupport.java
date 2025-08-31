package sharev;

import com.fasterxml.jackson.databind.ObjectMapper;
import sharev.account.service.AccountService;
import sharev.account.service.CustomOauth2UserService;
import sharev.card.service.CardService;
import sharev.card_connection.service.CardConnectionService;
import sharev.config.EmbeddedRedisConfig;
import sharev.config.JpaConfig;
import sharev.config.QuerydslConfig;
import sharev.config.RedisConfig;
import sharev.config.RedissonConfig;
import sharev.util.LockProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        QuerydslConfig.class,
        RedisConfig.class,
        RedissonConfig.class,
        EmbeddedRedisConfig.class,
        JpaConfig.class
}))
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected AccountService accountService;

    @MockitoBean
    protected CardService cardService;

    @MockitoBean
    protected CardConnectionService cardConnectionService;

    @MockitoBean
    protected CustomOauth2UserService customOauth2UserService;

    @MockitoBean
    protected LockProcessor lockProcessor;
}
