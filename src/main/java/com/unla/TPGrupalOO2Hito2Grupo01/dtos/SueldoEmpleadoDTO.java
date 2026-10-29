package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import java.math.BigDecimal;

public record SueldoEmpleadoDTO(String unidad,
                                String nombre,
                                String apellido,
                                String rol,         // "COCINERO" o "CAJERO"
                                String categoria,   // solo cocinero, null en cajero
                                BigDecimal sueldo) {
}
