package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.ReciboDetalladoEmpleadoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IUnidadVenta;
import com.unla.TPGrupalOO2Hito2Grupo01.utilidades.ReciboPdfService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.EmpleadoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cajero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cocinero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Turno;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.CategoriaCocina;
import com.unla.TPGrupalOO2Hito2Grupo01.helpers.ViewRouteHelper;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IEmpleadoService;

import jakarta.validation.Valid;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping("/empleado")
public class EmpleadoController {

    private final IEmpleadoService empleadoService;
    private final IUnidadVenta unidadVentaService;
    private final ReciboPdfService reciboPdfService;

    public EmpleadoController(IEmpleadoService empleadoService,IUnidadVenta unidadVentaService,
                              ReciboPdfService reciboPdfService) {
        this.empleadoService = empleadoService;
        this.unidadVentaService=unidadVentaService;
        this.reciboPdfService = reciboPdfService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE', 'EMPLEADO')")
    public String listEmpleados(Model model) {
        model.addAttribute("empleados", empleadoService.getAllActive());
        return ViewRouteHelper.EMPLEADO_INDEX;
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public String newEmpleado(Model model) {
        model.addAttribute("empleado", new EmpleadoDTO());
        cargarDatosFormulario(model);
        return ViewRouteHelper.EMPLEADO_NEW;//controller prepara un dto vacio y le pasa las opciones
    }                                       // de los enums para que la vista pueda construir el formulario

    @PostMapping("/create")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public String create(
            @Valid @ModelAttribute("empleado") EmpleadoDTO empleadoDTO,
            BindingResult bindingResult,
            Model model,
            Authentication authentication) {

        if (bindingResult.hasErrors()) {
            cargarDatosFormulario(model);
            return ViewRouteHelper.EMPLEADO_NEW;
        }

        Empleado empleado = crearEmpleadoDesdeDTO(empleadoDTO);

        if (empleadoDTO.getIdEmpleado() == null) {
            // Los nuevos empleados se crean activos.
            empleado.setActivo(true);
        } else {
            Empleado empleadoExistente = empleadoService.findById(
                    empleadoDTO.getIdEmpleado()
            );

            if (empleadoExistente == null) {
                model.addAttribute(
                        "error",
                        "No se encontró el empleado que se intenta modificar."
                );
                cargarDatosFormulario(model);
                return ViewRouteHelper.EMPLEADO_NEW;
            }

            empleado.setIdEmpleado(empleadoExistente.getIdEmpleado());

            // Al editar, se conserva el estado almacenado.
            empleado.setActivo(empleadoExistente.isActivo());

            // Se conserva la cuenta existente.
            empleado.setUser(empleadoExistente.getUser());
        }
        try { //si no hay ID es un alta y se ejecuta la creacion de usuario si hay id es una edicion y se conserva la cuenta existente
            if (empleado.getIdEmpleado() == null) {
                empleadoService.darDeAltaConUsuario(
                        empleado,
                        empleadoDTO.getEmail(),
                        authentication.getName()
                );
            } else {
                empleadoService.insertOrUpdate(empleado);
            }

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            cargarDatosFormulario(model);
            return ViewRouteHelper.EMPLEADO_NEW;
        }

        return "redirect:/responsable/empleados";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE')")
    public String editEmpleado(@PathVariable("id") int id, Model model) {

        Empleado empleado = empleadoService.findById(id);

        if (empleado == null) {
            return "redirect:/empleado";
        }

        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setIdEmpleado(empleado.getIdEmpleado());
        dto.setNombre(empleado.getNombre());
        dto.setApellido(empleado.getApellido());
        dto.setDni(empleado.getDni());
        dto.setFechaNacimiento(empleado.getFechaNacimiento());
        dto.setIngreso(empleado.getIngreso());
        dto.setActivo(empleado.isActivo());

        if (empleado.getUser() != null) {
            dto.setEmail(empleado.getUser().getUsername());
        }

        if (empleado instanceof Cajero) {
            dto.setTipoEmpleado("CAJERO");
            dto.setTurno(((Cajero) empleado).getTurno());

        } else if (empleado instanceof Cocinero) {
            dto.setTipoEmpleado("COCINERO");
            dto.setCategoria(((Cocinero) empleado).getCategoria());
            dto.setTareaEspecial(((Cocinero) empleado).getTareaEspecial());
        }

        model.addAttribute("empleado", dto);
        cargarDatosFormulario(model);

        return ViewRouteHelper.EMPLEADO_NEW;
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public String deleteLogico(@PathVariable("id") int id) {
        empleadoService.deleteLogico(id);
        return "redirect:/responsable/empleados";
    }

    private Empleado crearEmpleadoDesdeDTO(EmpleadoDTO empleadoDTO) { //reune los datos del formulario y
                                                                //crea la entidad segun el tipo elegido
        if ("CAJERO".equalsIgnoreCase(empleadoDTO.getTipoEmpleado())) {
            return new Cajero(
                    empleadoDTO.getNombre(),
                    empleadoDTO.getApellido(),
                    empleadoDTO.getDni(),
                    empleadoDTO.getFechaNacimiento(),
                    empleadoDTO.getIngreso(),
                    empleadoDTO.isActivo(),
                    empleadoDTO.getTurno()
            );
        }

        return new Cocinero(
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
    @PreAuthorize("hasRole('EMPLEADO')")
    @GetMapping("/sueldo")
    public ModelAndView traerSueldo(@AuthenticationPrincipal User user){
        ModelAndView mV = new ModelAndView();
        mV.setViewName("unidadventa/mi-sueldo");
        mV.addObject("recibo", unidadVentaService.sueldoEmpleado(user.getUsername()));
        return mV;
    }

    // Descarga del recibo en PDF. Ruta final: /empleado/sueldo/pdf
    @GetMapping("/sueldo/pdf")
    public void descargarSueldoPdf(@AuthenticationPrincipal User user,
                                   HttpServletResponse response) throws IOException {
        // Se obtiene siempre el recibo del empleado logueado, nunca por parametro en la URL
        ReciboDetalladoEmpleadoDTO recibo = unidadVentaService.sueldoEmpleado(user.getUsername());

        if (recibo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        byte[] pdf = reciboPdfService.generarRecibo(recibo);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"recibo-sueldo.pdf\"");
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
    }
    private void cargarDatosFormulario(Model model) {
        model.addAttribute("turnos", Turno.values());
        model.addAttribute("categorias", CategoriaCocina.values());
    }

    @PreAuthorize("hasRole('EMPLEADO')")
    @GetMapping("/mi-informacion")
    public String verMiInformacion(@AuthenticationPrincipal User user, Model model) {
        if (user == null) {
            return "redirect:/login";
        }

        Empleado empleado = empleadoService.traerPorUsername(user.getUsername());

        if (empleado == null) {
            return "redirect:/login";
        }

        model.addAttribute("empleado", empleado);

        if (empleado.getUnidadVenta() != null) {
            model.addAttribute("unidadVenta", empleado.getUnidadVenta());
            model.addAttribute("festival", empleado.getUnidadVenta().getFestival());
        } else {
            model.addAttribute("unidadVenta", null);
            model.addAttribute("festival", null);
        }

        return ViewRouteHelper.EMPLEADO_MI_INFORMACION;
    }



}