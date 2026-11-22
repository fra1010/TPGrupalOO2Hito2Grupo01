package com.unla.TPGrupalOO2Hito2Grupo01.controllers.api;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PedidoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Pedido;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUnidadVentaRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IPedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@PreAuthorize("hasAuthority('CAJERO')")
@Tag(name = "Pedidos API", description = "Endpoints REST para la gestión de pedidos")
public class PedidoApiController {

    private final IPedidoService pedidoService;
    private final IUnidadVentaRepository unidadVentaRepository;

    public PedidoApiController(
            IPedidoService pedidoService,
            IUnidadVentaRepository unidadVentaRepository) {
        this.pedidoService = pedidoService;
        this.unidadVentaRepository = unidadVentaRepository;
    }

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo pedido",
            description = "Crea un pedido reutilizando PedidoService.registrarPedido y aplicando las reglas del caso de uso " +
                    "(fecha actual dentro del festival, ítems válidos sin platos repetidos, platos activos de la unidad del cajero y subtotal precio de venta por cantidad)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Pedido registrado correctamente",
                    content = @Content(schema = @Schema(implementation = Pedido.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o regla de negocio incumplida"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: se requiere autoridad CAJERO"
            )
    })
    public ResponseEntity<?> postPedido(
            @Valid @RequestBody PedidoDTO pedidoDTO,
            Authentication authentication) {
        try {
            UnidadVenta unidad = obtenerUnidad(authentication);
            pedidoDTO.setIdUnidadVenta(unidad.getIdUnidadVenta());

            Pedido pedidoGuardado = pedidoService.registrarPedido(pedidoDTO, unidad);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoGuardado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    private UnidadVenta obtenerUnidad(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("No hay un cajero autenticado");
        }

        UnidadVenta unidad = unidadVentaRepository
                .findByCajeroUsername(authentication.getName());

        if (unidad == null) {
            throw new IllegalArgumentException(
                    "El cajero no tiene una unidad de venta asignada"
            );
        }

        return unidad;
    }
}