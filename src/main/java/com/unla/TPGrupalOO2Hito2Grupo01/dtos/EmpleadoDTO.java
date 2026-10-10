
package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpleadoDTO {

    private Integer idEmpleado;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Positive(message = "El DNI debe ser un número positivo")
    private long dni;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ingreso;

    private boolean activo;

    public EmpleadoDTO(
            Integer idEmpleado,
            String nombre,
            String apellido,
            long dni,
            LocalDate fechaNacimiento,
            LocalDate ingreso,
            boolean activo) {

        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.ingreso = ingreso;
        this.activo = activo;
    }
}