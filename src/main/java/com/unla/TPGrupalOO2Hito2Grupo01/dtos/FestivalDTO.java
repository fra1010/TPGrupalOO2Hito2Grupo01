package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

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
    private double costoSuperficies;

    @PositiveOrZero(message = "El costo no puede ser negativo")
    private double costoMontaje;

    @PositiveOrZero(message = "El costo no puede ser negativo")
    private double plusElectricidad;

    @PositiveOrZero(message = "El sueldo base no puede ser negativo")
    private double sueldoBase;

    public FestivalDTO(
            Integer idFestival,
            String nombre,
            String temporada,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            double costoSuperficies,
            double costoMontaje,
            double plusElectricidad,
            double sueldoBase) {

        this.idFestival = idFestival;
        this.nombre = nombre;
        this.temporada = temporada;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.costoSuperficies = costoSuperficies;
        this.costoMontaje = costoMontaje;
        this.plusElectricidad = plusElectricidad;
        this.sueldoBase = sueldoBase;
    }
}