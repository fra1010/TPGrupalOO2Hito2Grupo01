package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReporteUnidadDTO {
    private Integer idFestival;
    private String nombreFestival;
    private Integer idUnidad;
    private String nombreUnidad;
    private BigDecimal recaudacionTotal;
    private List<PlatoReporteDTO> platos;
}
