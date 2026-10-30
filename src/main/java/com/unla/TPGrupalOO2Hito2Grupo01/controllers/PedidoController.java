
package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.ItemPedidoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PedidoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUnidadVentaRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IPedidoService;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IPlatoService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


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
    public String listPedidos(Model model, Authentication authentication) {

        UnidadVenta unidad = obtenerUnidad(authentication);

        model.addAttribute("unidad", unidad);
        model.addAttribute("pedidos", pedidoService.getByUnidad(unidad.getIdUnidadVenta()));

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
            Authentication authentication) {

        UnidadVenta unidad = obtenerUnidad(authentication);

        if (bindingResult.hasErrors()) {
            cargarFormulario(model, pedidoDTO, unidad);
            return "pedido/new";
        }

        try {
            pedidoService.registrarPedido(pedidoDTO, unidad);

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
                    "El cajero no tiene una unidad de venta asignada");
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
