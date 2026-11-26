package com.unla.TPGrupalOO2Hito2Grupo01.controllers.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "com.unla.TPGrupalOO2Hito2Grupo01.controllers.api")
public class ManejadorExcepcionesApi
{
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> manejarErrorSeguridad(AccessDeniedException ex)
    {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();

        int estado;
        String error;
        String descripcion;

        if (autenticacion == null || !autenticacion.isAuthenticated() || autenticacion instanceof AnonymousAuthenticationToken)
        {
            estado = 401;
            error = "Usuario no logueado";
            descripcion = "Debe iniciar sesión en el sistema antes de realizar esta consulta.";

        }
        else
        {
            estado = 403;
            error = "Acceso denegado";
            descripcion = "Su usuario no tiene el rol CAJERO requerido para registrar pedidos.";
        }

        return ResponseEntity.status(estado).body(Map.of("estado", estado, "error", error, "descripcion", descripcion));
    }

    // Manejo los errores de validación de los datos enviados en el JSON (400)

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> manejarErrorValidacion(MethodArgumentNotValidException ex)
    {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Datos de entrada inválidos");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "estado", 400,
                "error", "Datos inválidos",
                "descripcion", detalle
        ));
    }

    // manejo los errores de logica de negocio (ej. plato inexistente o cantidad incorrecta) (400)

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> manejarArgumentoInvalido(IllegalArgumentException ex)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "estado", 400,
                "error", "Pedido incorrecto",
                "descripcion", ex.getMessage()
        ));
    }
}
