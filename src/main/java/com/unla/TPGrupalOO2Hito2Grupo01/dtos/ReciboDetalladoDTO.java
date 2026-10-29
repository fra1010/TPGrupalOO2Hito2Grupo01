package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
public class ReciboDetalladoDTO {
    // --- Identificación ---
    private final String unidadVenta;
    private final String nombre;
    private final String apellido;
    private final String tipoEmpleado;
    private final String categoria;

    // --- Período liquidado ---
    private final LocalDate liquidadoDesde;
    private final LocalDate liquidadoHasta;
    private final Long diasTrabajados;

    // --- Proporcionalidad ---
    private final Long quincenas;
    private final BigDecimal factor;

    // --- Haberes ---
    private final BigDecimal sueldoBase;
    private final String conceptoPlus;
    private final Integer cantidadPlus;
    private final BigDecimal valorUnitarioPlus;
    private final BigDecimal montoPlus;
    private final BigDecimal sueldoReferencia;

    // --- Resultado ---
    private final BigDecimal totalAPagar;
}
