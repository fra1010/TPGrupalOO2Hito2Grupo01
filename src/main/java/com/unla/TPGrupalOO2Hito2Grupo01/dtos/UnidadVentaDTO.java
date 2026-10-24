package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UnidadVentaDTO {
    // Lo genera la base de datos: es null en el formulario de alta.
    // En el formulario de edicion va como campo oculto para saber que registro actualizar.
    private Integer idUnidadVenta;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    // Wrapper (Double) para que @NotNull detecte cuando el campo no viene informado
    @NotNull(message = "La superficie es obligatoria")
    @Positive(message = "La superficie tiene que ser positiva")
    private Double superficie;

    // En lugar de la entidad Festival se expone solo su id
    private Integer idFestival;

//    @NotBlank(message = "El codigo es obligatorio")
//    @Size(max = 45, message = "El codigo no puede superar los 45 caracteres")
    private String codigo;

    // Se crea junto con la unidad de venta. @Valid hace que se validen tambien los campos de UserDTO.
    // Se inicializa para que th:field="*{responsable.username}" no falle cuando el formulario se abre vacio.
    @Valid
    private UserDTO responsable = new UserDTO();

    private List<EmpleadoDTO> empleados = new ArrayList<>();


}
