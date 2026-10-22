package com.unla.TPGrupalOO2Hito2Grupo01.dtos;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PuestoDesarmableDTO extends UnidadVentaDTO {
    // Wrapper (Integer) para que @NotNull detecte cuando el campo no viene informado

    @Positive(message = "La cantidad de carpas tiene que ser positiva")
    private int cantidadCarpas;


    @Positive(message = "El tiempo tiene que ser positivo")
    private int tiempo;
}
