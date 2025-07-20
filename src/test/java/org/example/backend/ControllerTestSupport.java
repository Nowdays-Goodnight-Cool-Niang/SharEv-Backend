package org.example.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.account.service.AccountService;
import org.example.backend.account.service.CustomOauth2UserService;
import org.example.backend.config.EmbeddedRedisConfig;
import org.example.backend.config.JpaConfig;
import org.example.backend.config.QuerydslConfig;
import org.example.backend.config.RedisConfig;
import org.example.backend.config.RedissonConfig;
import org.example.backend.profile.service.ProfileService;
import org.example.backend.relation.service.RelationService;
import org.example.backend.util.LockProcessor;
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
    protected ProfileService profileService;

    @MockitoBean
    protected RelationService relationService;

    @MockitoBean
    protected CustomOauth2UserService customOauth2UserService;

    @MockitoBean
    protected LockProcessor lockProcessor;
}
