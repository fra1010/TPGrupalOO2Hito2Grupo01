package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
public class PlatoReporteDTO {
    private Integer idPlato;
    private String plato;
    private Long cantidad;
    private BigDecimal totalRecaudado;
    private BigDecimal precioDeVenta;
}
