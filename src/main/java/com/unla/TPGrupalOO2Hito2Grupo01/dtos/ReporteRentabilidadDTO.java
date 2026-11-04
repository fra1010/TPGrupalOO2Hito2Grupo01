package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReporteRentabilidadDTO {

    private final Integer idUnidadVenta;
    private final String nombreUnidad;
    private final String nombreFestival;

    private final LocalDate desde;
    private final LocalDate hasta;

    private final long diasPeriodo;
    private final long diasFestival;

    private final BigDecimal ingresosPedidos;
    private final BigDecimal costosPlatos;
    private final BigDecimal sueldos;

    private final BigDecimal canonTotal;
    private final BigDecimal canonPeriodo;

    private final BigDecimal rentabilidadNeta;
}