package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.*;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Festival;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Role;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IFestivalService;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IUnidadVenta;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Controller
@RequestMapping("/unidadVenta")
public class UnidadVentaController {
    private final IUnidadVenta unidadVentaService;
    private  final IUser userService;
    private  final IFestivalService festivalService;

    public UnidadVentaController(IUnidadVenta unidadVentaService,IUser userService,IFestivalService festivalService) {
        this.userService=userService;
        this.unidadVentaService = unidadVentaService;
        this.festivalService=festivalService;
    }

    @GetMapping("/unidades")
    public ModelAndView unidadesVenta(){
        List<UnidadVentaDTO> unidades = unidadVentaService.traerUnidadesVenta();
        ModelAndView mV = new ModelAndView();
        mV.setViewName("unidadventa/unidadventa-lista");
        mV.addObject("unidades",unidades);
        return mV;
    }
    @GetMapping("/formulariounidadventa")
    @PreAuthorize("isAnonymous()")
    public ModelAndView unidadventa(){
        List<Festival> festivales= festivalService.getAllActive();
        ModelAndView mV = new ModelAndView();
        mV.setViewName("unidadventa/formulario-unidad-venta");
        mV.addObject("unidadVenta", new UnidadVentaFormDTO());
        mV.addObject("festivales", festivales);
        return mV;
    }
    @PostMapping("/crearunidadventa")
    @PreAuthorize("isAnonymous()")
    public String unidadVenta(
            @Valid @ModelAttribute("unidadVenta") UnidadVentaFormDTO unidadVenta,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("festivales", festivalService.getAllActive());

            return "unidadventa/formulario-unidad-venta"; // vista (forward, no redirect)
        }


        try {

            UserDTO responsable = userService.agregarUser(unidadVenta.getResponsable(), Role.RESPONSABLE);
            unidadVentaService.agregarUnidadVenta(unidadVenta, responsable);

        } catch (Exception e) {
            // Se muestra en la alerta ${error} del formulario
            model.addAttribute("error", e.getMessage());
            model.addAttribute("festivales", festivalService.getAllActive());
            return "unidadventa/formulario-unidad-venta";
        }
        return "redirect:/index";
    }



}
