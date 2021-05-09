/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mt.tech.river.bankingdemo.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.swagger2.annotations.EnableSwagger2
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.spi.DocumentationType
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.builders.ApiInfoBuilder

/**
 * Swagger configuration.
 *
 * Set the following properties in the `application.properties`:
 *
 *  * swagger.title_ws: Web-Service title
 *  * swagger.description_ws: Web-Service description
 *  * swagger.version_ws: Web-Service version
 *  * swagger.package_controllers: package where are the controllers located.
 */
@Configuration
@EnableSwagger2
@Suppress("unused")
class SwaggerConfig {
    @Value("\${swagger.title_ws: Web-Service title}")
    private val title: String? = null

    @Value("\${swagger.version: 2.0.1}")
    private val version: String? = null

    @Value("\${swagger.description_ws: Web-Service description missing.}")
    private val description: String? = null

    @Value("\${swagger.package_controllers: com.sample.controllers}")
    private val packageControllers: String? = null

    private fun apiEndPointsInfo(): ApiInfo = ApiInfoBuilder().title(this.title).
            description(this.description).version(this.version).build()

    @Bean
    fun api(): Docket = Docket(DocumentationType.SWAGGER_2).select().apis(
            RequestHandlerSelectors.basePackage(this.packageControllers)).paths(
            PathSelectors.any()).build().apiInfo(this.apiEndPointsInfo())
}