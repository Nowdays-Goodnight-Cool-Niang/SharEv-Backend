package sharev

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippet
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.test.web.servlet.MockMvc
import sharev.config.SecurityConfig

@WebMvcTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(
    SecurityConfig::class,
    UseCaseMockRegistrar::class,
)
abstract class ControllerTestSupport {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var applicationContext: ApplicationContext

    @BeforeEach
    fun resetUseCaseMocks() {
        applicationContext.beanDefinitionNames
            .filter { it.endsWith("UseCase") }
            .map { applicationContext.getBean(it) }
            .filter { Mockito.mockingDetails(it).isMock }
            .forEach { Mockito.reset(it) }
    }

    protected inline fun <reified T : Any> mockBean(): T = applicationContext.getBean<T>()

    protected fun documentResource(
        identifier: String,
        resourceSnippet: ResourceSnippet
    ): RestDocumentationResultHandler =
        MockMvcRestDocumentationWrapper.document(identifier, snippets = arrayOf(resourceSnippet))

    protected fun schema(name: String): Schema = Schema.schema(name)
}
