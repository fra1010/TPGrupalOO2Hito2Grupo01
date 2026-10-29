package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ReporteCanonDTO {
    private Integer unidadVentaId;
    private String tipo;
    private String unidadNombre;
    private String festivalNombre;
    private BigDecimal superficie;
    private BigDecimal costoPorM2;
    private BigDecimal subtotalSuperficie;

    // Food truck
    @Getter(AccessLevel.NONE)
    private Integer conexion;            // 1 = con conexión, 0 = sin conexión
    private BigDecimal plusElectricidad;

    // Puesto desarmable
    private BigDecimal tiempo;
    private BigDecimal costoMontaje;
    private BigDecimal subtotalMontaje;

    private BigDecimal total;

    public boolean isConexion() {
        return conexion != null && conexion == 1;
    }
}
