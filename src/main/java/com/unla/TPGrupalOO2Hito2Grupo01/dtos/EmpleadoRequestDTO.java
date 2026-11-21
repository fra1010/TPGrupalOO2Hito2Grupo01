package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.CategoriaCocina;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Turno;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Datos para dar de alta un empleado en una unidad de venta")
public class EmpleadoRequestDTO {

    @NotNull(message = "La unidad de venta es obligatoria")
    @Positive(message = "El id de la unidad de venta debe ser positivo")
    @Schema(description = "Id de la unidad de venta a la que se asigna", example = "1")
    private Integer idUnidadVenta;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(example = "Rocío")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Schema(example = "Salinas")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Ingrese un email válido")
    @Size(max = 45, message = "El email no puede superar los 45 caracteres")
    @Schema(description = "Será el usuario de acceso; las credenciales se envían a esta casilla",
            example = "mail@gmail.com")
    private String email;

    @NotNull(message = "El DNI es obligatorio")
    @Positive(message = "El DNI debe ser un número positivo")
    @Schema(example = "40123456")
    private Long dni;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Schema(example = "1995-04-12")
    private LocalDate fechaNacimiento;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    @Schema(example = "2026-10-01")
    private LocalDate ingreso;

    @NotBlank(message = "Debe indicar el tipo de empleado")
    @Pattern(regexp = "(?i)CAJERO|COCINERO", message = "El tipo de empleado debe ser CAJERO o COCINERO")
    @Schema(allowableValues = {"CAJERO", "COCINERO"}, example = "CAJERO")
    private String tipoEmpleado;

    @Schema(description = "Obligatorio si el tipo es CAJERO", example = "MANANA")
    private Turno turno;

    @Schema(description = "Obligatoria si el tipo es COCINERO", example = "COCINERO")
    private CategoriaCocina categoria;

    @Schema(description = "Solo para cocineros", example = "Parrilla")
    private String tareaEspecial;

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "El empleado debe tener al menos 18 años cumplidos")
    public boolean isMayorDeEdad() {
        if (fechaNacimiento == null) {
            return true; // ya lo valida @NotNull
        }
        return !fechaNacimiento.isAfter(LocalDate.now().minusYears(18));
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "Un cajero debe tener turno (MANANA o NOCHE)")
    public boolean isTurnoValido() {
        if (!"CAJERO".equalsIgnoreCase(tipoEmpleado)) {
            return true;
        }
        return turno != null;
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "Un cocinero debe tener categoría (COCINERO, COCINERO_AYUDANTE o LAVAPLATOS)")
    public boolean isCategoriaValida() {
        if (!"COCINERO".equalsIgnoreCase(tipoEmpleado)) {
            return true;
        }
        return categoria != null;
    }
}