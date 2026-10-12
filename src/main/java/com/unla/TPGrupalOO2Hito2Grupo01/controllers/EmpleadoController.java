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
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cajero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cocinero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Turno;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.CategoriaCocina;
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
        // Enviamos las opciones de los enums para renderizar los selects en la vista
        model.addAttribute("turnos", Turno.values());
        model.addAttribute("categorias", CategoriaCocina.values());
        return ViewRouteHelper.EMPLEADO_NEW;
    }

    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("empleado") EmpleadoDTO empleadoDTO,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            // Si hay errores, volvemos a cargar los enums antes de retornar a la vista
            model.addAttribute("turnos", Turno.values());
            model.addAttribute("categorias", CategoriaCocina.values());
            return ViewRouteHelper.EMPLEADO_NEW;
        }

        Empleado empleado;

        // Evaluamos el tipo de empleado seleccionado en el formulario para construir la entidad correspondiente
        if ("CAJERO".equalsIgnoreCase(empleadoDTO.getTipoEmpleado())) {
            empleado = new Cajero(
                    empleadoDTO.getNombre(),
                    empleadoDTO.getApellido(),
                    empleadoDTO.getDni(),
                    empleadoDTO.getFechaNacimiento(),
                    empleadoDTO.getIngreso(),
                    empleadoDTO.isActivo(),
                    empleadoDTO.getTurno()
            );
        } else {
            empleado = new Cocinero(
                    empleadoDTO.getNombre(),
                    empleadoDTO.getApellido(),
                    empleadoDTO.getDni(),
                    empleadoDTO.getFechaNacimiento(),
                    empleadoDTO.getIngreso(),
                    empleadoDTO.isActivo(),
                    empleadoDTO.getCategoria(),
                    empleadoDTO.getTareaEspecial()
            );
        }

        // Mantenemos el ID si estamos procesando una edición
        if (empleadoDTO.getIdEmpleado() != null) {
            empleado.setIdEmpleado(empleadoDTO.getIdEmpleado());
        }

        empleadoService.insertOrUpdate(empleado);

        // Cambiado a redirect para actualizar la URL limpiamente en el navegador
        return "redirect:/empleado";
    }
}
