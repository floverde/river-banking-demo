package mt.tech.river.bankingdemo.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean

/**
 * OpenAPI Swagger 3 configuration.
 *
 * Set the following properties in the `application.properties`:
 * * springdoc.info.title: Web-Service title
 * * springdoc.info.description: Web-Service description
 * * springdoc.info.version: Web-Service version
 * * springdoc.info.licence.name: Software licence.
 * * springdoc.info.licence.url: Software licence URL.
 * * springdoc.info.termsOfService: Terms of services.
 *
 * @author floverde
 * @version 1.0
 */
@Configuration
@Suppress("unused")
class SwaggerConfig {
    @Value("\${springdoc.info.title: Web-Service title}")
    private val title: String? = null

    @Value("\${springdoc.info.description: Web-Service description missing.}")
    private val description: String? = null

    @Value("\${springdoc.info.version: 1.0.0}")
    private val version: String? = null

    @Value("\${springdoc.info.licence.name: Apache 2.0}")
    private val licenceName: String? = null

    @Value("\${springdoc.info.licence.url: http://springdoc.org}")
    private val licenceURL: String? = null

    @Value("\${springdoc.info.terms-of-service: http://swagger.io/terms/}")
    private val termsOfService: String? = null

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI().info(Info().title(this.title).version(this.version).
                description(this.description).termsOfService(this.termsOfService).
                license(License().name(this.licenceName).url(this.licenceURL)))
    }
}