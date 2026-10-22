package com.unla.TPGrupalOO2Hito2Grupo01.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.unla.TPGrupalOO2Hito2Grupo01.services.IEmailService;

@Configuration
public class PruebaEmailConfiguration {

    private static final Logger log =
            LoggerFactory.getLogger(PruebaEmailConfiguration.class);

    @Bean
    @ConditionalOnProperty(
            name = "app.mail.prueba.enabled",
            havingValue = "true"
    )
    CommandLineRunner probarEnvioEmail(
            IEmailService emailService,
            @Value("${app.mail.prueba.destinatario}") String destinatario) {

        return args -> {
            log.info("PRUEBA SMTP: iniciando intento de envío.");

            emailService.enviarCredencialesEmpleado(
                    destinatario,
                    "PRUEBA SMTP - No se creó ningún empleado",
                    destinatario,
                    "SOLO-PRUEBA-NO-VALIDA"
            );

            log.info(
                    "Prueba SMTP completada: el servidor aceptó el envío. "
                            + "Verificar recepción en la casilla de destino."
            );
        };
    }
}