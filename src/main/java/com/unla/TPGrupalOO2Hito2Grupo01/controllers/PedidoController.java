package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.ItemPedidoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PedidoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Pedido;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUnidadVentaRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IPedidoService;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IPlatoService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/pedido")
@PreAuthorize("hasAuthority('CAJERO')")
public class PedidoController {

    private final IPedidoService pedidoService;
    private final IUnidadVentaRepository unidadVentaRepository;
    private final IPlatoService platoService;

    public PedidoController(
            IPedidoService pedidoService,
            IUnidadVentaRepository unidadVentaRepository,
            IPlatoService platoService) {

        this.pedidoService = pedidoService;
        this.unidadVentaRepository = unidadVentaRepository;
        this.platoService = platoService;
    }

    @GetMapping
    public String listPedidos(
            @RequestParam(name = "pagina", defaultValue = "0")
            int pagina,
            @RequestParam(name = "numeroPedido", required = false)
            Integer numeroPedido,
            Model model,
            Authentication authentication) {

        UnidadVenta unidad = obtenerUnidad(authentication);

        model.addAttribute("unidad", unidad);
        model.addAttribute("numeroPedido", numeroPedido);
        model.addAttribute("busqueda", numeroPedido != null);

        if (numeroPedido != null) {

            List<Pedido> pedidosEncontrados =
                    numeroPedido > 0
                            ? pedidoService.buscarPorNumeroYUnidad(
                            numeroPedido,
                            unidad.getIdUnidadVenta()
                    ).map(List::of).orElseGet(List::of)
                            : List.of();

            model.addAttribute("pedidos", pedidosEncontrados);

            model.addAttribute(
                    "fecha",
                    pedidosEncontrados.isEmpty()
                            ? null
                            : pedidosEncontrados.get(0).getFecha()
            );

            model.addAttribute(
                    "dias",
                    Page.<LocalDate>empty()
            );

            return "pedido/index";
        }

        Page<LocalDate> dias = pedidoService.getFechasConPedidos(
                unidad.getIdUnidadVenta(),
                Math.max(pagina, 0)
        );

        if (!dias.hasContent() && pagina > 0) {
            dias = pedidoService.getFechasConPedidos(
                    unidad.getIdUnidadVenta(),
                    0
            );
        }

        LocalDate fecha = dias.hasContent()
                ? dias.getContent().get(0)
                : null;

        List<Pedido> pedidos = fecha == null
                ? List.of()
                : pedidoService.getByUnidadYFecha(
                unidad.getIdUnidadVenta(),
                fecha
        );

        model.addAttribute("dias", dias);
        model.addAttribute("fecha", fecha);
        model.addAttribute("pedidos", pedidos);

        return "pedido/index";
    }

    @GetMapping("/new")
    public String newPedido(
            Model model,
            Authentication authentication) {

        UnidadVenta unidad = obtenerUnidad(authentication);

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setIdUnidadVenta(unidad.getIdUnidadVenta());
        pedidoDTO.getItems().add(new ItemPedidoDTO());

        cargarFormulario(model, pedidoDTO, unidad);

        return "pedido/new";
    }

    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("pedido") PedidoDTO pedidoDTO,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        UnidadVenta unidad = obtenerUnidad(authentication);

        if (bindingResult.hasErrors()) {
            cargarFormulario(model, pedidoDTO, unidad);
            return "pedido/new";
        }

        try {
            Pedido pedidoGuardado = pedidoService.registrarPedido(
                    pedidoDTO,
                    unidad
            );

            redirectAttributes.addFlashAttribute(
                    "exito",
                    "El pedido #" + pedidoGuardado.getIdPedido()
                            + " fue registrado correctamente."
            );
            return "redirect:/pedido";

        } catch (IllegalArgumentException exception) {
            bindingResult.reject(
                    "pedido",
                    exception.getMessage()
            );

            cargarFormulario(model, pedidoDTO, unidad);

            return "pedido/new";
        }
    }

    private UnidadVenta obtenerUnidad(Authentication authentication) {

        UnidadVenta unidad = unidadVentaRepository
                .findByCajeroUsername(authentication.getName());

        if (unidad == null) {
            throw new IllegalArgumentException(
                    "El cajero no tiene una unidad de venta asignada"
            );
        }

        return unidad;
    }

    private void cargarFormulario(
            Model model,
            PedidoDTO pedidoDTO,
            UnidadVenta unidad) {

        model.addAttribute("pedido", pedidoDTO);
        model.addAttribute("unidad", unidad);

        model.addAttribute(
                "platos",
                platoService.getActivosByUnidad(
                        unidad.getIdUnidadVenta()
                )
        );
    }
}