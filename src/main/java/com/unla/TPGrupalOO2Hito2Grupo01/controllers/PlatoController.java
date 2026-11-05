package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PlatoFormDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.UnidadVentaDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IPlatoService;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IUnidadVenta;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/responsable/platos")
@PreAuthorize("hasRole('RESPONSABLE')")
public class PlatoController {

    private final IPlatoService platoService;
    private final IUnidadVenta unidadVentaService;

    public PlatoController(
            IPlatoService platoService,
            IUnidadVenta unidadVentaService) {

        this.platoService = platoService;
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping
    public String listar(
            @AuthenticationPrincipal User user,
            Model model) {

        model.addAttribute("platos", List.of());

        try {
            cargarNombreUnidad(user.getUsername(), model);

            model.addAttribute(
                    "platos",
                    platoService.getActivosDelResponsable(
                            user.getUsername()
                    )
            );

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "plato/platos-lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(
            @AuthenticationPrincipal User user,
            Model model) {

        model.addAttribute("plato", new PlatoFormDTO());

        try {
            cargarNombreUnidad(user.getUsername(), model);

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("altaDeshabilitada", true);
        }

        return "plato/plato-formulario";
    }

    @PostMapping
    public String registrar(
            @Valid @ModelAttribute("plato")
            PlatoFormDTO formulario,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            cargarNombreUnidad(user.getUsername(), model);

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("altaDeshabilitada", true);

            return "plato/plato-formulario";
        }

        if (bindingResult.hasErrors()) {
            return "plato/plato-formulario";
        }

        try {
            platoService.registrarPlato(
                    formulario,
                    user.getUsername()
            );

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());

            return "plato/plato-formulario";
        }

        redirectAttributes.addFlashAttribute(
                "exito",
                "El plato fue registrado correctamente."
        );

        return "redirect:/responsable/platos";
    }

    private void cargarNombreUnidad(
            String username,
            Model model) {

        UnidadVentaDTO unidad =
                unidadVentaService.traerUnidadVentaResponsable(
                        username
                );

        if (unidad == null) {
            throw new IllegalArgumentException(
                    "No tenés una unidad de venta asignada."
            );
        }

        model.addAttribute(
                "nombreUnidad",
                unidad.getNombre()
        );
    }
}