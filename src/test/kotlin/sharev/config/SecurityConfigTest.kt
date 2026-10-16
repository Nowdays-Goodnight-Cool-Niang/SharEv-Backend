package sharev.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import sharev.ControllerTestSupport

class SecurityConfigTest : ControllerTestSupport() {
    @Test
    @DisplayName("비로그인 사용자가 보호된 API를 호출하면 401만 응답하고 세션을 만들지 않는다")
    fun unauthenticatedRequest_doesNotCreateSession() {
        val result = mockMvc.perform(get("/accounts"))
            .andExpect(status().isUnauthorized)
            .andReturn()

        assertThat(result.request.getSession(false)).isNull()
    }
}
