package sharev.common.adapter.inbound.web.advice

import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import sharev.gathering.adapter.inbound.web.dto.request.GetGatheringRequest

class InvalidRequestExceptionAdviceTest {

    @RestController
    class TestController {
        @GetMapping("/test/gatherings")
        fun search(request: GetGatheringRequest): String = "ok"
    }

    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(TestController())
        .setControllerAdvice(InvalidRequestExceptionAdvice())
        .build()

    @Test
    fun `enum 필드 변환 실패 시 허용값 목록을 반환한다`() {
        mockMvc.perform(get("/test/gatherings").param("visibility", "purple"))
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.visibility[0]").value("허용되는 값: public, private"))
    }

    @Test
    fun `비-enum 필드 변환 실패 시 기본 메시지를 반환한다`() {
        mockMvc.perform(get("/test/gatherings").param("teamId", "not-a-number"))
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.teamId[0]").value("유효한 값이 아닙니다."))
    }
}
