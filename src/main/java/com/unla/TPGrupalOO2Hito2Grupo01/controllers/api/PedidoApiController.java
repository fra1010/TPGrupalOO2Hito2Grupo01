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

    // -------------------------API DELETE ELIMINAR PEDIDO-------------------------------

    @DeleteMapping("/{idPedido}")
    @Operation(summary = "Eliminar pedido")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Pedido eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Pedido inválido o inexistente",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Mensaje de error\"}"
                            )
                    )
            )
    })
    public ResponseEntity<?> deletePedido(
            @PathVariable Integer idPedido,
            Authentication authentication) {

        try {

            UnidadVenta unidad = unidadVentaService
                    .findByCajeroUsername(authentication.getName());

            pedidoService.eliminarPedido(
                    idPedido,
                    unidad.getIdUnidadVenta()
            );

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException ex) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
