package com.unla.TPGrupalOO2Hito2Grupo01.services;

public interface IEmailService {

    void enviarCredencialesEmpleado(
            String destinatario,
            String nombre,
            String username,
            String passwordGenerada
    );
}