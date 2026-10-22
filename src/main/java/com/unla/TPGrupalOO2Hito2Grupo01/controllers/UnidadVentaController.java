package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.FoodTruckDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PuestoDesarmableDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.UnidadVentaDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IUnidadVenta;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@RequestMapping("/unidadVenta")
public class UnidadVentaController {
    private final IUnidadVenta unidadVentaService;

    public UnidadVentaController(IUnidadVenta unidadVentaService) {

        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping("/formularioFoodTruck")
    public ModelAndView unidadVentaFoodTruck(Model model){
        ModelAndView mV = new ModelAndView();
        mV.setViewName("unidadventa/formulario-foodtruck");
        mV.addObject("foodTruckDTO", new FoodTruckDTO());
        return mV;

    }
    @GetMapping("/formularioPuestoDesarmable")
    public ModelAndView unidadVentaPuestoDesarmable(Model model){
        ModelAndView mV = new ModelAndView();
        mV.setViewName("unidadventa/formulario-puesto-desarmable");
        mV.addObject("puestoDesarmableDTO", new PuestoDesarmableDTO());
        return mV;

    }
    @PostMapping("/nuevafoodtruck")
    public RedirectView unidadVenta(@Valid @ModelAttribute("unidadVenta")FoodTruckDTO foodTruck , BindingResult bindingResult){
        RedirectView rV = new RedirectView();
        if(bindingResult.hasErrors()){
            rV.setUrl("/unidadventa/formulario-foodtruck");
        }
        else {
            unidadVentaService.agregarOActualizarUnidadVenta(foodTruck);
            rV.setUrl("/unidadventa/unidades");
        }
        return rV;
    }
    @PostMapping("/nuevapuestodesarmable")
    public RedirectView unidadVenta(@Valid @ModelAttribute("unidadVenta")PuestoDesarmableDTO puestoDesarmable , BindingResult bindingResult){
        RedirectView rV = new RedirectView();
        if(bindingResult.hasErrors()){
            rV.setUrl("/unidadventa/formulario-puesto-desarmable");
        }
        else {
            unidadVentaService.agregarOActualizarUnidadVenta(puestoDesarmable);
            rV.setUrl("/unidadventa/unidades");
        }
        return rV;
    }
    @GetMapping("/unidades")
    public ModelAndView unidadesVenta(){
        List<UnidadVentaDTO> unidades = unidadVentaService.traerUnidadesVenta();
        ModelAndView mV = new ModelAndView();
        mV.setViewName("unidadventa/unidadventa-lista");
        mV.addObject("unidades",unidades);
        return mV;
    }
}
