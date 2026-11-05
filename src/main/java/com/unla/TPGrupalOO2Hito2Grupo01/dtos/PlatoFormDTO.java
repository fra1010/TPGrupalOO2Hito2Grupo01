package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PlatoFormDTO {

    @NotBlank(message = "El nombre del plato es obligatorio")
    @Size(
            max = 255,
            message = "El nombre no puede superar los 255 caracteres"
    )
    private String nombre;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "El precio de venta debe ser mayor a cero"
    )
    @Digits(
            integer = 10,
            fraction = 2,
            message = "El precio admite hasta 10 dígitos enteros y 2 decimales"
    )
    private BigDecimal precioDeVenta;

    @NotNull(message = "El costo de producción es obligatorio")
    @DecimalMin(
            value = "0.0",
            message = "El costo de producción no puede ser negativo"
    )
    @Digits(
            integer = 10,
            fraction = 2,
            message = "El costo admite hasta 10 dígitos enteros y 2 decimales"
    )
    private BigDecimal costoDePlato;
}