package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.SueldoEmpleadoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.UnidadVentaDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IEmpleadoService;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IUnidadVenta;

@Controller
@RequestMapping("/responsable")
@PreAuthorize("hasRole('RESPONSABLE')")
public class ResponsableController {

    private final IUnidadVenta unidadVentaService;
    private final IEmpleadoService empleadoService;

    public ResponsableController(
            IUnidadVenta unidadVentaService,
            IEmpleadoService empleadoService) {

        this.unidadVentaService = unidadVentaService;
        this.empleadoService = empleadoService;
    }

    @GetMapping("/canon")
    public ModelAndView obtenerCanon(
            @AuthenticationPrincipal User user) {

        ModelAndView mV = new ModelAndView();

        BigDecimal canon =
                unidadVentaService.calcularCanon(user.getUsername());

        mV.setViewName("unidadventa/canon");
        mV.addObject("canon", canon);

        return mV;
    }

    @GetMapping("/empleados")
    public ModelAndView empleadosUnidad(
            @AuthenticationPrincipal User user) {

        ModelAndView mV = new ModelAndView();

        UnidadVentaDTO unidad =
                unidadVentaService.traerUnidadVentaConEmpleados(
                        user.getUsername()
                );

        mV.addObject("unidad", unidad);
        mV.setViewName("unidadventa/unidad-venta-empleados");

        return mV;
    }

    @PostMapping("/empleados/activar/{id}")
    public String activarEmpleado(
            @PathVariable("id") Integer idEmpleado,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {

        try {
            empleadoService.activarEmpleado(
                    idEmpleado,
                    user.getUsername()
            );

            redirectAttributes.addFlashAttribute(
                    "exito",
                    "El empleado fue activado correctamente."
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/responsable/empleados";
    }

    @PostMapping("/empleados/estado/{id}")
    public String cambiarEstadoEmpleado(
            @PathVariable("id") Integer idEmpleado,
            @RequestParam(name = "activo", defaultValue = "false")
            boolean activo,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {

        try {
            empleadoService.cambiarEstadoEmpleado(
                    idEmpleado,
                    activo,
                    user.getUsername()
            );

            redirectAttributes.addFlashAttribute(
                    "exito",
                    activo
                            ? "El empleado fue activado correctamente."
                            : "El empleado fue desactivado correctamente."
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/responsable/empleados";
    }


    @GetMapping("/haberes")
    public ModelAndView obtenerSueldosUnidadVenta(
            @AuthenticationPrincipal User user) {

        ModelAndView mV = new ModelAndView();

        List<SueldoEmpleadoDTO> sueldos =
                unidadVentaService.calcularSueldoUnidadVenta(
                        user.getUsername()
                );

        mV.setViewName("unidadventa/unidadventa-sueldos");
        mV.addObject("sueldos", sueldos);

        return mV;
    }
}