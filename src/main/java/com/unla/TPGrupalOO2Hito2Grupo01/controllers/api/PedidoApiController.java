package com.unla.TPGrupalOO2Hito2Grupo01.controllers.api;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PedidoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PedidoRespuestaDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Pedido;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IPedidoService;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IUnidadVenta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@PreAuthorize("hasAuthority('CAJERO')")
@Tag(name = "Pedidos API", description = "Gestión de pedidos")
public class PedidoApiController {

    private final IPedidoService pedidoService;
    private final IUnidadVenta unidadVentaService;

    public PedidoApiController(
            IPedidoService pedidoService,
            IUnidadVenta unidadVentaService) {

        this.pedidoService = pedidoService;
        this.unidadVentaService = unidadVentaService;
    }

    // -----------------------METODO PUT PEDIDO (EDITAR PEDIDO)-------------------------------

    @PutMapping("/{idPedido}")
    @Operation(summary = "Modificar pedido")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido modificado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido no encontrado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado"
            )
    })
    public ResponseEntity<?> putPedido(
            @PathVariable Integer idPedido,
            @Valid @RequestBody PedidoDTO pedidoDTO,
            Authentication authentication) {

        try {

            UnidadVenta unidad = unidadVentaService
                    .findByCajeroUsername(authentication.getName());

            pedidoDTO.setIdUnidadVenta(
                    unidad.getIdUnidadVenta()
            );

            Pedido pedidoActualizado = pedidoService.actualizarPedido(
                    idPedido,
                    pedidoDTO,
                    unidad
            );

            return ResponseEntity.ok(
                    new PedidoRespuestaDTO(pedidoActualizado)
            );

        } catch (IllegalArgumentException ex) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }


}
