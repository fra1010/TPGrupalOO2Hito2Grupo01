
package com.unla.TPGrupalOO2Hito2Grupo01.controllers.api;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IPedidoService;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IUnidadVenta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
public class PedidoApiController
{
    private final IPedidoService pedidoService;
    private final IUnidadVenta unidadVentaService;

    public PedidoApiController(IPedidoService pedidoService, IUnidadVenta unidadVentaService)
    {
        this.pedidoService = pedidoService;
        this.unidadVentaService = unidadVentaService;
    }

    // -----------------------METODO DELETE PEDIDO (ELIMINAR PEDIDO)-------------------------------

    @DeleteMapping("/{idPedido}")
    @Operation(summary = "Eliminar pedido")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Pedido eliminado correctamente. La operacion no devuelve contenido."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "No se pudo eliminar el pedido porque los datos son invalidos o el pedido no existe.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Mensaje de error\"}"
                            )
                    )
            )
    })

    public ResponseEntity<?> deletePedido(@PathVariable Integer idPedido, Authentication authentication)
    {
        ResponseEntity<?> respuesta;

        try
        {
            UnidadVenta unidad = unidadVentaService.findByCajeroUsername(authentication.getName());
            pedidoService.eliminarPedido(idPedido, unidad.getIdUnidadVenta());

            respuesta = ResponseEntity.noContent().build();

        }
        catch (IllegalArgumentException ex)
        {
            respuesta = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }

        return respuesta;
    }

}
