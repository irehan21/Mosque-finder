package com.mosquefinder.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mosque Finder API")
                        .version("1.0")
                        .description("API documentation for Mosque Finder application")
                        .contact(new Contact()
                                .name("Rehan Khan")
                                .email("rehaankhan21976@gmail.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Mosque Finder API Documentation")
                        .url("https://github.com/irehan21/Mosque-finder"));
    }
}
