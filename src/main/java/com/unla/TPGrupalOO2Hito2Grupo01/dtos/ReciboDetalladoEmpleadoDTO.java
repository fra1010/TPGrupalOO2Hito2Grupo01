package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@AllArgsConstructor
public class ReciboDetalladoEmpleadoDTO {
    private String unidadVenta;
    private String nombre;
    private String apellido;
    private String rol;
    private String categoria;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Long dias;
    private Long quincenas;
    private BigDecimal meses;
    private BigDecimal sueldoBase;
    private String descripcionPlus;
    private Integer antiguedad;
    private BigDecimal valorPlus;
    private BigDecimal totalPlus;
    private BigDecimal bruto;
    private BigDecimal total;

    public long getDiasRestantes() {
        return Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), fechaHasta));
    }
}
