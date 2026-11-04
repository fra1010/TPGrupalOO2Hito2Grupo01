package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PlatoReporteDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestParam;

import com.unla.TPGrupalOO2Hito2Grupo01.services.IUnidadVenta;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.FestivalDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Costo;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Festival;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IFestivalService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/festival")
public class FestivalController {

    private final IFestivalService festivalService;
    private final IUnidadVenta unidadVentaService;

    public FestivalController(IFestivalService festivalService,
                              IUnidadVenta unidadVentaService) {
        this.festivalService = festivalService;
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping("/new")
    public String newFestival(Model model) {
        model.addAttribute("festival", new FestivalDTO());
        return "festival/new";
    }

    @GetMapping("/{idFestival}/unidades")
    @PreAuthorize("hasRole('ADMIN')")
    public String unidadesDeFestival(
            @PathVariable("idFestival") Integer idFestival,
            @RequestParam(name = "x", defaultValue = "3") int x,
            Model model) {

        Festival festival = festivalService.findById(idFestival)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el festival indicado."
                ));

        model.addAttribute("festival", festival);
        model.addAttribute("x", x);

        return "festival/unidades";
    }

    @GetMapping("/{idFestival}/unidades/lista")
    @PreAuthorize("hasRole('ADMIN')")
    public String unidadesLista(
            @PathVariable("idFestival") Integer idFestival,
            Model model) {

        Festival festival = festivalService.findById(idFestival)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el festival indicado."
                ));

        model.addAttribute("festival", festival);
        model.addAttribute("unidades",
                unidadVentaService.traerUnidadesPorFestival(idFestival));

        return "festival/unidades-lista";
    }

    @GetMapping("/{idFestival}/ranking")
    @PreAuthorize("hasRole('ADMIN')")
    public String rankingUnidades(
            @PathVariable("idFestival") Integer idFestival,
            Model model) {

        Festival festival = festivalService.findById(idFestival)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el festival indicado."
                ));

        model.addAttribute("festival", festival);
        model.addAttribute("unidades",
                festivalService.rankingUnidadesPorRecaudacion(idFestival));
        model.addAttribute("recaudaciones",
                festivalService.recaudacionesPorFestival(idFestival));

        return "festival/ranking";
    }

    @GetMapping("/{idFestival}/mayores-canon")
    @PreAuthorize("hasRole('ADMIN')")
    public String mayoresCanon(
            @PathVariable("idFestival") Integer idFestival,
            @RequestParam(name = "x", defaultValue = "3") int x,
            Model model) {

        Festival festival = festivalService.findById(idFestival)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el festival indicado."
                ));

        model.addAttribute("festival", festival);
        model.addAttribute("x", x);
        model.addAttribute("reporte",
                unidadVentaService.unidadesConMayorCanon(idFestival, x));

        return "festival/mayores-canon";
    }

    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("festival") FestivalDTO festivalDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "festival/new";
        }

        try {
            Costo costo = new Costo(
                    festivalDTO.getCostoSuperficies(),
                    festivalDTO.getCostoMontaje(),
                    festivalDTO.getPlusElectricidad(),
                    festivalDTO.getSueldoBase(),
                    festivalDTO.getPlusAntiguedad(),
                    festivalDTO.getPlusCocinero(),
                    festivalDTO.getPlusAyudante(),
                    festivalDTO.getPlusLavaplatos()
            );

            Festival festival = new Festival(
                    festivalDTO.getNombre(),
                    festivalDTO.getTemporada(),
                    festivalDTO.getFechaInicio(),
                    festivalDTO.getFechaFin(),
                    costo
            );

            festivalService.insertOrUpdate(festival);

            return "redirect:/";

        } catch (IllegalArgumentException exception) {
            bindingResult.reject("festival", exception.getMessage());
            return "festival/new";
        }
    }

    @GetMapping("/{idFestival}/unidades/{idUnidad}/plato-estrella")
    @PreAuthorize("hasRole('ADMIN')")
    public String platoEstrella(
            @PathVariable("idFestival") Integer idFestival,
            @PathVariable("idUnidad") Integer idUnidad,
            Model model) {

        Festival festival = festivalService.findById(idFestival)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el festival indicado."
                ));

        PlatoReporteDTO platoEstrella = festivalService.obtenerPlatoEstrella(idFestival, idUnidad);

        model.addAttribute("festival", festival);
        model.addAttribute("idUnidad", idUnidad);
        model.addAttribute("platoEstrella", platoEstrella);

        return "festival/plato-estrella";
    }

}