package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Pedido;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PedidoRespuestaDTO {

    private Integer idPedido;
    private LocalDate fecha;
    private Integer idUnidadVenta;
    private String nombreUnidadVenta;
    private List<ItemPedidoResponseDTO> items;
    private BigDecimal total;

    public PedidoRespuestaDTO(Pedido pedido) {
        this.idPedido = pedido.getIdPedido();
        this.fecha = pedido.getFecha();
        if (pedido.getUnidadVenta() != null) {
            this.idUnidadVenta = pedido.getUnidadVenta().getIdUnidadVenta();
            this.nombreUnidadVenta = pedido.getUnidadVenta().getNombre();
        }
        this.items = pedido.getItemsPedido() != null
                ? pedido.getItemsPedido().stream().map(ItemPedidoResponseDTO::new).toList()
                : List.of();
        this.total = pedido.getTotal();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemPedidoResponseDTO {
        private Integer idItemPedido;
        private Integer idPlato;
        private String nombrePlato;
        private Integer cantidad;
        private BigDecimal totalItem;

        public ItemPedidoResponseDTO(com.unla.TPGrupalOO2Hito2Grupo01.entities.ItemPedido item) {
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
