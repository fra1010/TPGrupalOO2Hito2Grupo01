package com.unla.TPGrupalOO2Hito2Grupo01.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Epicentro Gourmet API")
                .version("1.0")
                .description("API REST del sistema de gestión de festivales"));
    }

    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder()
                .group("api")
                .packagesToScan("com.unla.TPGrupalOO2Hito2Grupo01.controllers.api")
                .build();
    }
}