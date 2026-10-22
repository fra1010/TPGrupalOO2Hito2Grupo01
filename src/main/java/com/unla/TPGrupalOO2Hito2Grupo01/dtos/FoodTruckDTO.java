package com.unla.TPGrupalOO2Hito2Grupo01.dtos;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FoodTruckDTO extends UnidadVentaDTO{
    @NotBlank(message = "La patente es obligatoria")
    private String patente;

    private boolean conexion;
}
