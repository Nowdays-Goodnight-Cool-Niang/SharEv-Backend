package sharev.gathering.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class IntroductionFormContentTest {
    @Test
    @DisplayName("빈 소개 템플릿은 유효하다")
    fun emptyTemplate_isValid() {
        val template = Template("", emptyMap())

        assertThat(template.source).isEmpty()
        assertThat(template.fields).isEmpty()
    }
}
