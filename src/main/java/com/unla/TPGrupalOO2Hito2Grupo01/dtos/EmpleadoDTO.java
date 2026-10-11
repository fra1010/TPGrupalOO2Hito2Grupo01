package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Turno;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.CategoriaCocina;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpleadoDTO
{
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

    // ----- nuevos campos para la herencia -----

    @NotBlank(message = "Debe seleccionar el tipo de empleado")
    private String tipoEmpleado; // "CAJERO" o "COCINERO"

    private Turno turno; // Se usará si tipoEmpleado es "CAJERO"

    private CategoriaCocina categoria; // Se usará si tipoEmpleado es "COCINERO"

    private String tareaEspecial; // Se usará si tipoEmpleado es "COCINERO"

    public EmpleadoDTO(Integer idEmpleado, String nombre, String apellido, long dni,
                       LocalDate fechaNacimiento, LocalDate ingreso, boolean activo,
                       String tipoEmpleado, Turno turno, CategoriaCocina categoria, String tareaEspecial)
    {
        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.ingreso = ingreso;
        this.activo = activo;
        this.tipoEmpleado = tipoEmpleado;
        this.turno = turno;
        this.categoria = categoria;
        this.tareaEspecial = tareaEspecial;
    }
}
