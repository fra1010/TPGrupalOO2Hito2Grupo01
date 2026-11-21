package com.unla.TPGrupalOO2Hito2Grupo01.controllers.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Solo aplica a los controllers del paquete api: no afecta a los MVC de Thymeleaf.
@RestControllerAdvice(basePackages = "com.unla.TPGrupalOO2Hito2Grupo01.controllers.api")
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        return respuestaError(List.of(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(
            MethodArgumentNotValidException ex) {

        List<String> mensajes = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        : error.getDefaultMessage())
                .toList();

        return respuestaError(mensajes);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonInvalido(
            HttpMessageNotReadableException ex) {

        return respuestaError(List.of(
                "El cuerpo de la petición no es válido. Revise el formato del JSON, "
                        + "las fechas (yyyy-MM-dd) y los valores permitidos "
                        + "(turno: MANANA o NOCHE; categoría: COCINERO, "
                        + "COCINERO_AYUDANTE o LAVAPLATOS)."
        ));
    }

    private ResponseEntity<Map<String, Object>> respuestaError(List<String> errores) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "errores", errores
                ));
    }
}