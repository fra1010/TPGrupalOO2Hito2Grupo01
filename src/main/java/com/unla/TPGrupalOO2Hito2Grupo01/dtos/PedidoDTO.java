
package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PedidoDTO {

    @NotNull(message = "Debe seleccionar una unidad de venta")
    private Integer idUnidadVenta;

    @Valid
    @NotEmpty(message = "El pedido debe contener al menos un plato")
    private List<ItemPedidoDTO> items = new ArrayList<>();
}