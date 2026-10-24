package com.unla.TPGrupalOO2Hito2Grupo01.services;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.SueldoEmpleadoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.UnidadVentaDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.UnidadVentaFormDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.UserDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface IUnidadVenta {
    public List<UnidadVentaDTO> traerUnidadesVenta();
    public BigDecimal calcularCanon(String username);
    public UnidadVentaDTO traerUnidadVentaConEmpleados(String username);
    public List<SueldoEmpleadoDTO> calcularSueldoUnidadVenta(String username);
    public void agregarUnidadVenta(UnidadVentaFormDTO unidadVenta, UserDTO responsable);
    public  UnidadVenta traerUnidadVentaResponsable(String username );

}
