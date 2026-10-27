package com.unla.TPGrupalOO2Hito2Grupo01.services;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.*;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IUnidadVenta {
    public List<UnidadVentaDTO> traerUnidadesVenta();
    public ReporteCanonDTO calcularCanon(String username);
    public UnidadVentaDTO traerUnidadVentaConEmpleados(String username);
    List<ReciboDetalladoDTO>  calcularSueldoUnidadVenta(String username, LocalDate desde,LocalDate hasta);
    public void agregarUnidadVenta(UnidadVentaFormDTO unidadVenta, UserDTO responsable);
    public  UnidadVentaDTO traerUnidadVentaResponsable(String username );
    public ReciboDetalladoEmpleadoDTO sueldoEmpleado(String username);
    public UnidadVenta findByCajeroUsername(String username);
}
