package com.unla.TPGrupalOO2Hito2Grupo01.services;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.UnidadVentaDTO;

import java.math.BigDecimal;
import java.util.List;

public interface IUnidadVenta {
    public List<UnidadVentaDTO> traerUnidadesVenta();
    public void agregarOActualizarUnidadVenta(UnidadVentaDTO unidadVenta);
    public BigDecimal calcularCanon(String username);
}
