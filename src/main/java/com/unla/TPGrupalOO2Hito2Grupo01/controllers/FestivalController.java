package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.unla.TPGrupalOO2Hito2Grupo01.services.IFestivalService;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Costo;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Festival;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.FestivalDTO;

@Controller
@RequestMapping("/festival")
public class FestivalController {

    private final IFestivalService festivalService;

    public FestivalController(IFestivalService festivalService) {
        this.festivalService = festivalService;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("festivales", festivalService.getAll());
        return "festival/index";
    }

    @GetMapping("/new")
    public String newFestival(Model model) {
        model.addAttribute("festival", new FestivalDTO());
        return "festival/new";
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute("festival") FestivalDTO festivalDTO) {

        Costo costo = new Costo(
                festivalDTO.getCostoSuperficies(),
                festivalDTO.getCostoMontaje(),
                festivalDTO.getPlusElectricidad(),
                festivalDTO.getSueldoBase()
        );

        Festival festival = new Festival(
                festivalDTO.getNombre(),
                festivalDTO.getTemporada(),
                festivalDTO.getFechaInicio(),
                festivalDTO.getFechaFin(),
                costo
        );

        festivalService.insertOrUpdate(festival);

        return "redirect:/festival";
    }
}