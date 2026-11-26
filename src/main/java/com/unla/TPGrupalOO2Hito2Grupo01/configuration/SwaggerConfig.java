package com.unla.TPGrupalOO2Hito2Grupo01.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig
{
    @Bean
    public OpenAPI openAPI()
    {
        return new OpenAPI().info(new Info()
                        .title("Epicentro Gourmet | API REST")
                        .version("1.0.0")
                        .description(
                                "API REST del sistema Epicentro Gourmet. "
                                        + "Permite gestionar las operaciones del sistema "
                                        + "de festivales gastronómicos, incluyendo el "
                                        + "registro de pedidos."
                        )
                        .contact(new Contact().name("Grupo 01 - OO2"))
                );
    }

    @Bean
    public GroupedOpenApi apiGroup()
    {
        return GroupedOpenApi.builder().group("api").packagesToScan("com.unla.TPGrupalOO2Hito2Grupo01.controllers.api").build();
    }
}
