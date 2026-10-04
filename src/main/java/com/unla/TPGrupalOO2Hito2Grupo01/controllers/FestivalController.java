package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IFestivalService;

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

        } catch (IllegalArgumentException exception) {
            bindingResult.reject("festival", exception.getMessage());
            return "festival/new";
        }
    }
}