

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

    @PostMapping
    @Operation(summary = "Registrar pedido")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = PedidoDTO.class),
                    examples = @ExampleObject(value = """
                            {
                              "idUnidadVenta": 1,
                              "items": [
                                {
                                  "idPlato": 1,
                                  "cantidad": 10
                                }
                              ]
                            }
                            """)
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Pedido registrado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = PedidoRespuestaDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o pedido incorrecto"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: se requiere el rol CAJERO"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<?> postPedido(
            @Valid @RequestBody PedidoDTO pedidoDTO,
            Authentication authentication) {

        try {
            UnidadVenta unidad = unidadVentaService
                    .findByCajeroUsername(authentication.getName());

            pedidoDTO.setIdUnidadVenta(unidad.getIdUnidadVenta());

            Pedido pedidoGuardado =
                    pedidoService.registrarPedido(pedidoDTO, unidad);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new PedidoRespuestaDTO(pedidoGuardado));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
