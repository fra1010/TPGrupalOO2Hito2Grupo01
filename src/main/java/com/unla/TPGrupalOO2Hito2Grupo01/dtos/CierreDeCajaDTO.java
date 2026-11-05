package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CierreDeCajaDTO {
    private LocalDate fecha;
    private String nombreUnidad;
    private BigDecimal totalRecaudado;
    private int cantidadPedidos;

    // --- NUEVOS CAMPOS PARA MAYOR DETALLE ---

    // El ticket promedio (total recaudado / cantidad de pedidos)
    private BigDecimal ticketPromedio;

    // Lista con los pedidos simplificados del día para la tabla
    private List<PedidoCierreDTO> pedidosDelDia;

    // Desglose de platos vendidos: Clave = Nombre del Plato, Valor = Cantidad total vendida
    private Map<String, Integer> platosVendidos;
}