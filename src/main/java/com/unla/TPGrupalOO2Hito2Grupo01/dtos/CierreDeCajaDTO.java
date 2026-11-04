package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CierreDeCajaDTO {
    private LocalDate fecha;
    private String nombreUnidad;
    private BigDecimal totalRecaudado;
    private int cantidadPedidos;

}
