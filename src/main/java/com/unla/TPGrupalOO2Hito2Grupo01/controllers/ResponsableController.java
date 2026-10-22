package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.SueldoEmpleadoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.UnidadVentaDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IUnidadVenta;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/responsable")
@PreAuthorize("hasRole('ROLE_RESPONSABLE')")
public class ResponsableController {

    private final IUnidadVenta unidadVentaService;

    public ResponsableController(IUnidadVenta unidadVentaService) {
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping("/canon")
    public ModelAndView obtenerCanon(@AuthenticationPrincipal User user){
        ModelAndView mV = new ModelAndView();
        BigDecimal canon = null;
        canon = unidadVentaService.calcularCanon(user.getUsername());

        mV.setViewName("unidadventa/canon");
        mV.addObject("canon", canon);
        return mV;
    }
    @GetMapping("/empleados")
    public ModelAndView empleadosUnidad(@AuthenticationPrincipal User user){
        ModelAndView mV = new ModelAndView();
        UnidadVentaDTO unidad= unidadVentaService.traerUnidadVentaConEmpleados(user.getUsername());
        mV.addObject("unidad",unidad);
        mV.setViewName("unidadventa/unidad-venta-empleados");

        return mV;
    }
    @GetMapping("/haberes")
    public ModelAndView obtenerSueldosUnidadVenta(@AuthenticationPrincipal User user){
        ModelAndView mV = new ModelAndView();
        mV.setViewName("unidadventa/unidadventa-sueldos");
        List<SueldoEmpleadoDTO> sueldos = unidadVentaService.calcularSueldoUnidadVenta(user.getUsername());
        mV.addObject("sueldos",sueldos);
        return mV;
    }
}
