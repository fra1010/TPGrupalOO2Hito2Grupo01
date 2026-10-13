package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
public class EmpleadoController
{
    private final IEmpleadoService empleadoService;

    public EmpleadoController(IEmpleadoService empleadoService)
    {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public String listEmpleados(Model model)
    {
        model.addAttribute("empleados", empleadoService.getAllActive());
        return ViewRouteHelper.EMPLEADO_INDEX;
    }

    @GetMapping("/new")
    public String newEmpleado(Model model)
    {
        model.addAttribute("empleado", new EmpleadoDTO());
        model.addAttribute("turnos", Turno.values());
        model.addAttribute("categorias", CategoriaCocina.values());
        return ViewRouteHelper.EMPLEADO_NEW;
    }

    @PostMapping("/create")
    public String create (@Valid @ModelAttribute("empleado") EmpleadoDTO empleadoDTO,
            BindingResult bindingResult, Model model)
    {

        if (bindingResult.hasErrors())
        {
            model.addAttribute("turnos", Turno.values());
            model.addAttribute("categorias", CategoriaCocina.values());
            return ViewRouteHelper.EMPLEADO_NEW;
        }

        Empleado empleado;

        if ("CAJERO".equalsIgnoreCase(empleadoDTO.getTipoEmpleado()))
        {
            empleado = new Cajero(empleadoDTO.getNombre(), empleadoDTO.getApellido(), empleadoDTO.getDni(),
                    empleadoDTO.getFechaNacimiento(), empleadoDTO.getIngreso(), empleadoDTO.isActivo(), empleadoDTO.getTurno());
        }
        else
        {
            empleado = new Cocinero(empleadoDTO.getNombre(), empleadoDTO.getApellido(), empleadoDTO.getDni(),
                    empleadoDTO.getFechaNacimiento(), empleadoDTO.getIngreso(),
                    empleadoDTO.isActivo(), empleadoDTO.getCategoria(), empleadoDTO.getTareaEspecial()
            );
        }

        if (empleadoDTO.getIdEmpleado() != null)
        {
            empleado.setIdEmpleado(empleadoDTO.getIdEmpleado());
        }

        // Bloque try-catch para atrapar el error si el DNI ya esta activo
        try
        {
            empleadoService.insertOrUpdate(empleado);
        }
        catch (IllegalArgumentException e)
        {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("turnos", Turno.values());
            model.addAttribute("categorias", CategoriaCocina.values());
            return ViewRouteHelper.EMPLEADO_NEW;
        }

        return "redirect:/empleado";
    }

    // Endpoint para cargar la pantalla de edicion con los datos del empleado
    @GetMapping("/edit/{id}")
    public String editEmpleado(@PathVariable("id") int id, Model model)
    {
        Empleado empleado = empleadoService.findById(id);

        if (empleado == null)
        {
            return "redirect:/empleado";
        }

        // Pasamos los datos del objeto persistido al DTO del formulario

        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setIdEmpleado(empleado.getIdEmpleado());
        dto.setNombre(empleado.getNombre());
        dto.setApellido(empleado.getApellido());
        dto.setDni(empleado.getDni());
        dto.setFechaNacimiento(empleado.getFechaNacimiento());
        dto.setIngreso(empleado.getIngreso());
        dto.setActivo(empleado.isActivo());

        // Mapeamos los campos especificos segun la instancia de la herencia
        if (empleado instanceof Cajero)
        {
            dto.setTipoEmpleado("CAJERO");
            dto.setTurno(((Cajero) empleado).getTurno());
        }
        else if (empleado instanceof Cocinero)
        {
            dto.setTipoEmpleado("COCINERO");
            dto.setCategoria(((Cocinero) empleado).getCategoria());
            dto.setTareaEspecial(((Cocinero) empleado).getTareaEspecial());
        }

        model.addAttribute("empleado", dto);
        model.addAttribute("turnos", Turno.values());
        model.addAttribute("categorias", CategoriaCocina.values());

        return ViewRouteHelper.EMPLEADO_NEW; // Reutiliza la misma vista de alta
    }

    // Endpoint para ejecutar la baja logica al tocar eliminar
    @GetMapping("/delete/{id}")
    public String deleteLogico(@PathVariable("id") int id)
    {
        empleadoService.deleteLogico(id);
        return "redirect:/empleado";
    }
}
