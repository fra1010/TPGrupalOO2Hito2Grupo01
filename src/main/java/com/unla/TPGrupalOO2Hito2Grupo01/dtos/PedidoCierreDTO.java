package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PedidoCierreDTO {
    private Long idPedido;
    private LocalDate fecha;
    private BigDecimal total; // Opcional, por si querés mostrar el subtotal de cada pedido en la tabla
}


