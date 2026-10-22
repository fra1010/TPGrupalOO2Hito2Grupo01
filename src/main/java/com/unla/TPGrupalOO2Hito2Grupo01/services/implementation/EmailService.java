package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import com.unla.TPGrupalOO2Hito2Grupo01.services.IEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    private final String remitente;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String remitente) {

        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    @Override
    public void enviarCredencialesEmpleado(
            String destinatario,
            String nombre,
            String username,
            String passwordGenerada) {

        SimpleMailMessage mensaje = new SimpleMailMessage();

        mensaje.setFrom(remitente);
        mensaje.setTo(destinatario);
        mensaje.setSubject("Epicentro Gourmet - Credenciales de acceso");

        mensaje.setText("""
                Hola %s:

                Se creó tu usuario de empleado en Epicentro Gourmet.

                Usuario: %s
                Contraseña: %s
                
                """.formatted(nombre, username, passwordGenerada));

        mailSender.send(mensaje);
    }
}