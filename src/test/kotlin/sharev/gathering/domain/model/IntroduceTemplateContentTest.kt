package sharev.gathering.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IntroduceTemplateContentTest {
    @Test
    fun `빈 소개 템플릿 본문은 유효하다`() {
        val content = IntroduceTemplateContent("", emptyMap())

        assertThat(content.text).isEmpty()
        assertThat(content.fieldPlaceholders).isEmpty()
    }
}
