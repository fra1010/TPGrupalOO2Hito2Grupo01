package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FestivalDTO {

    private Integer idFestival;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La temporada es obligatoria")
    private String temporada;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    @PositiveOrZero(message = "El costo no puede ser negativo")
    private BigDecimal costoSuperficies;

    @PositiveOrZero(message = "El costo no puede ser negativo")
    private BigDecimal costoMontaje;

    @PositiveOrZero(message = "El plus no puede ser negativo")
    private BigDecimal plusElectricidad;

    @PositiveOrZero(message = "El sueldo base no puede ser negativo")
    private BigDecimal sueldoBase;

    @PositiveOrZero(message = "El plus no puede ser negativo")
    private BigDecimal plusAntiguedad;

    @PositiveOrZero(message = "El plus no puede ser negativo")
    private BigDecimal plusCocinero;

    @PositiveOrZero(message = "El plus no puede ser negativo")
    private BigDecimal plusAyudante;

    @PositiveOrZero(message = "El plus no puede ser negativo")
    private BigDecimal plusLavaplatos;

    public FestivalDTO(
            Integer idFestival,
            String nombre,
            String temporada,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            BigDecimal costoSuperficies,
            BigDecimal costoMontaje,
            BigDecimal plusElectricidad,
            BigDecimal sueldoBase,
            BigDecimal plusAntiguedad,
            BigDecimal plusCocinero,
            BigDecimal plusAyudante,
            BigDecimal plusLavaplatos) {

        this.idFestival = idFestival;
        this.nombre = nombre;
        this.temporada = temporada;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.costoSuperficies = costoSuperficies;
        this.costoMontaje = costoMontaje;
        this.plusElectricidad = plusElectricidad;
        this.sueldoBase = sueldoBase;
        this.plusAntiguedad = plusAntiguedad;
        this.plusCocinero = plusCocinero;
        this.plusAyudante = plusAyudante;
        this.plusLavaplatos = plusLavaplatos;
    }
}