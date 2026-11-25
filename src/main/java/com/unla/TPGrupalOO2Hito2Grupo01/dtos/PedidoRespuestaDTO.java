
package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Pedido;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Respuesta con los datos del pedido registrado")
public class PedidoRespuestaDTO {

    @Schema(description = "Identificador único del pedido", example = "6153")
    private Integer idPedido;

    @Schema(description = "Fecha en que se registró el pedido", example = "2026-11-23")
    private LocalDate fecha;

    @Schema(description = "Identificador de la unidad de venta", example = "1")
    private Integer idUnidadVenta;

    @Schema(description = "Nombre de la unidad de venta", example = "Burger Wheels")
    private String nombreUnidadVenta;

    @Schema(description = "Listado de platos incluidos en el pedido")
    private List<ItemPedidoResponseDTO> items;

    @Schema(description = "Importe total del pedido", example = "65000.00")
    private BigDecimal total;

    public PedidoRespuestaDTO(Pedido pedido) {
        this.idPedido = pedido.getIdPedido();
        this.fecha = pedido.getFecha();

        if (pedido.getUnidadVenta() != null) {
            this.idUnidadVenta = pedido.getUnidadVenta().getIdUnidadVenta();
            this.nombreUnidadVenta = pedido.getUnidadVenta().getNombre();
        }

        this.items = pedido.getItemsPedido() != null
                ? pedido.getItemsPedido().stream()
                .map(ItemPedidoResponseDTO::new)
                .toList()
                : List.of();

        this.total = pedido.getTotal();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(description = "Detalle de un plato incluido en el pedido")
    public static class ItemPedidoResponseDTO {

        @Schema(description = "Identificador del ítem del pedido", example = "12303")
        private Integer idItemPedido;

        @Schema(description = "Identificador del plato", example = "1")
        private Integer idPlato;

        @Schema(description = "Nombre del plato", example = "Hamburguesa clásica")
        private String nombrePlato;

        @Schema(description = "Cantidad de unidades solicitadas", example = "10")
        private Integer cantidad;

        @Schema(description = "Importe total correspondiente a este ítem", example = "65000.00")
        private BigDecimal totalItem;

        public ItemPedidoResponseDTO(
                com.unla.TPGrupalOO2Hito2Grupo01.entities.ItemPedido item) {

            this.idItemPedido = item.getIdItemPedido();

            if (item.getPlato() != null) {
                this.idPlato = item.getPlato().getIdPlato();
                this.nombrePlato = item.getPlato().getNombre();
            }

            this.cantidad = item.getCantidad();
            this.totalItem = item.getTotalItem();
        }
    }
}
