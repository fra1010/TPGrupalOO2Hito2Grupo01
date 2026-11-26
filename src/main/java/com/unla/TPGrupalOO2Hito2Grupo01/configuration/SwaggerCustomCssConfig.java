
package com.unla.TPGrupalOO2Hito2Grupo01.configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServletRequest;

import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;
import org.springdoc.core.providers.ObjectMapperProvider;

@Configuration
public class SwaggerCustomCssConfig
{
    @Bean
    public SwaggerIndexTransformer swaggerIndexTransformer(
            SwaggerUiConfigProperties swaggerUiConfig,
            SwaggerUiOAuthProperties swaggerUiOAuth,
            SwaggerUiConfigParameters configParameters,
            SwaggerWelcomeCommon swaggerWelcome,
            ObjectMapperProvider objectMapperProvider)
    {

        return new SwaggerIndexPageTransformer(
                swaggerUiConfig,
                swaggerUiOAuth,
                configParameters,
                swaggerWelcome,
                objectMapperProvider)
        {

            @Override
            public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformer)
                    throws IOException
            {

                Resource transformed = super.transform(request, resource, transformer);

                if (resource.toString().contains("index.html"))
                {
                    String html = StreamUtils.copyToString(transformed.getInputStream(), StandardCharsets.UTF_8);

                    html = html.replace(
                            "</head>",
                            "<link rel=\"stylesheet\" " +
                                    "href=\"/css/swagger_pedido.css\">" +
                                    "</head>");

                    return new TransformedResource(transformed, html.getBytes(StandardCharsets.UTF_8));
                }

                return transformed;
            }
        };
    }

}
