package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import java.math.BigDecimal;

public class ReporteMayoresCanonDTO {
    private String nombreComercial;
    private String codigo;
    private String tipoUnidad;
    private BigDecimal canon;

    public ReporteMayoresCanonDTO(String nombreComercial, String codigo,
                                  String tipoUnidad, BigDecimal canon) {
        this.nombreComercial = nombreComercial;
        this.codigo = codigo;
        this.tipoUnidad = tipoUnidad;
        this.canon = canon;
    }

    public String getNombreComercial() { return nombreComercial; }
    public String getCodigo() { return codigo; }
    public String getTipoUnidad() { return tipoUnidad; }
    public BigDecimal getCanon() { return canon; }
}