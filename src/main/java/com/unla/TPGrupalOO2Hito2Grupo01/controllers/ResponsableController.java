package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import com.unla.TPGrupalOO2Hito2Grupo01.services.IUnidadVenta;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;

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
}
