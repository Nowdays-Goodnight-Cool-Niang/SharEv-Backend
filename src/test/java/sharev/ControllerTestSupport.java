package sharev;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sharev.config.EmbeddedRedisConfig;
import sharev.config.JpaConfig;
import sharev.config.QuerydslConfig;
import sharev.config.RedisConfig;
import sharev.config.RedissonConfig;
import sharev.domain.account.service.AccountService;
import sharev.domain.account.service.CustomOauth2UserService;
import sharev.domain.card.service.CardService;
import sharev.domain.connection.service.ConnectionService;
import sharev.domain.team.service.TeamService;
import sharev.util.LockProcessor;

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
    protected ConnectionService connectionService;

    @MockitoBean
    protected TeamService teamService;

    @MockitoBean
    protected CustomOauth2UserService customOauth2UserService;

    @MockitoBean
    protected LockProcessor lockProcessor;
}
