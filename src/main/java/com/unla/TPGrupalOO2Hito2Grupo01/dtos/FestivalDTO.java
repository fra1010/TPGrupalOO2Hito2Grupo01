package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FestivalDTO {

    private Integer idFestival;

    private String nombre;

    private String temporada;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    private double costoSuperficies;

    private double costoMontaje;

    private double plusElectricidad;

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