package org.example.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.account.service.AccountService;
import org.example.backend.profile.service.ProfileService;
import org.example.backend.relation.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ControllerTestSupport {

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
}
