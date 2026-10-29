package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.ReciboDetalladoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.ReporteCanonDTO;
import org.springframework.format.annotation.DateTimeFormat;
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

        ReporteCanonDTO reporte  =
                unidadVentaService.calcularCanon(user.getUsername());

        mV.setViewName("unidadventa/canon");
        mV.addObject("reporte", reporte);

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
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "false") boolean finFestival) {

        LocalDate hoy = LocalDate.now();

        // 1) Valores por defecto: del día 1 del mes actual hasta hoy.
        //    hastaFinal = null significa "hasta el último día del festival": la consulta lo
        //    resuelve con coalesce(:hasta, f.fechaFin).
        LocalDate desdeFinal = (desde != null) ? desde : hoy.withDayOfMonth(1);
        LocalDate hastaFinal = finFestival ? null : (hasta != null ? hasta : hoy);

        // 2) Validación del rango (con fin de festival no hay 'hasta' que validar)
        if (hastaFinal != null && desdeFinal.isAfter(hastaFinal)) {
            return vistaConError(desdeFinal, hastaFinal,
                    "El período no es válido: la fecha 'Desde' no puede ser posterior a 'Hasta'.");
        }

        // 3) Unidad de venta del responsable logueado (principal.getName() es el username)
        UnidadVentaDTO unidad = unidadVentaService.traerUnidadVentaResponsable(user.getUsername());

        if (unidad == null) {
            return vistaConError(desdeFinal, hastaFinal, "No tenés una unidad de venta asignada.");
        }

        // 4) Cálculo de los recibos para el período
        List<ReciboDetalladoDTO> recibos =
                unidadVentaService.calcularSueldoUnidadVenta(user.getUsername(), desdeFinal, hastaFinal);

        // 5) Fecha 'hasta' que se muestra en el formulario: la que realmente se liquidó.
        //    Si 'hasta' es null (fin del festival) o superó el fin del festival, la consulta liquidó
        //    hasta el último día del festival. Todos los recibos de la unidad comparten festival,
        //    así que alcanza con el primero.
        LocalDate hastaMostrada = hastaFinal;
        if (!recibos.isEmpty()) {
            LocalDate finLiquidado = recibos.get(0).getLiquidadoHasta();
            if (hastaFinal == null || hastaFinal.isAfter(finLiquidado)) {
                hastaMostrada = finLiquidado;
            }
        }

        ModelAndView mav = new ModelAndView("unidadventa/unidadventa-sueldos");
        mav.addObject("desde", desdeFinal);
        mav.addObject("hasta", hastaMostrada);
        mav.addObject("recibos", recibos);
        // true si el período incluye días posteriores a hoy: los importes son una proyección
        mav.addObject("proyeccion", hastaMostrada != null && hastaMostrada.isAfter(hoy));

        return mav;
    }

    @GetMapping("/reportes")
    public String reportes() {
        return "unidadventa/reportes";
    }

    @GetMapping("/recaudacion")
    public ModelAndView recaudacionUnidadVenta(@AuthenticationPrincipal User user){
        ModelAndView mV = new ModelAndView();
        mV.setViewName("unidadventa/reporte-recaudacion");
        mV.addObject("reportes", unidadVentaService.reporteUnidadVenta(user.getUsername()));


        return mV;
    }
    private ModelAndView vistaConError(LocalDate desde, LocalDate hasta, String mensaje) {
        ModelAndView mav = new ModelAndView("unidadventa/unidadventa-sueldos");
        mav.addObject("desde", desde);
        mav.addObject("hasta", hasta);
        mav.addObject("recibos", List.of());
        mav.addObject("error", mensaje);
        return mav;
    }
}