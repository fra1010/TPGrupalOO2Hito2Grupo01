package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.EmpleadoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;
import com.unla.TPGrupalOO2Hito2Grupo01.helpers.ViewRouteHelper;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IEmpleadoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/empleado")
public class EmpleadoController {

    private final IEmpleadoService empleadoService;

    public EmpleadoController(IEmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public String listEmpleados(Model model) {
        model.addAttribute("empleados", empleadoService.getAllActive());
        return ViewRouteHelper.EMPLEADO_INDEX;
    }

    @GetMapping("/new")
    public String newEmpleado(Model model) {
        model.addAttribute("empleado", new EmpleadoDTO());
        return ViewRouteHelper.EMPLEADO_NEW;
    }

    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("empleado") EmpleadoDTO empleadoDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ViewRouteHelper.EMPLEADO_NEW;
        }

        // Creamos la entidad mapeando los tipos de datos exactos de Empleado.java
        Empleado empleado = new Empleado(
                empleadoDTO.getNombre(),
                empleadoDTO.getApellido(),
                (long) empleadoDTO.getDni(), // Casteo seguro a long para la entidad de base de datos
                empleadoDTO.getFechaNacimiento(),
                empleadoDTO.getIngreso(),
                empleadoDTO.isActivo()
        );

        empleadoService.insertOrUpdate(empleado);

        return ViewRouteHelper.EMPLEADO_ROOT;
    }
}
