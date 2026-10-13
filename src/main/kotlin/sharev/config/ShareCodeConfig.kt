package sharev.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sharev.card.domain.model.ShareCodeConverter
import java.util.*

@Configuration
class ShareCodeConfig {

    @Bean
    fun shareCodeConverter(@Value($$"${sharev.share-code.secret}") secret: String): ShareCodeConverter =
        ShareCodeConverter(Base64.getDecoder().decode(secret))
}
