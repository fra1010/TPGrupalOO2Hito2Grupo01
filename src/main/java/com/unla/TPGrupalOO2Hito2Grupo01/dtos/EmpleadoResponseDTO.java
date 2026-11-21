package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import java.time.LocalDate;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cajero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.CategoriaCocina;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cocinero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Turno;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Datos de un empleado")
public class EmpleadoResponseDTO {

    private Integer idEmpleado;
    private String nombre;
    private String apellido;
    private long dni;
    private LocalDate fechaNacimiento;
    private LocalDate ingreso;
    private boolean activo;
    private String email;
    private String tipoEmpleado;
    private Turno turno;
    private CategoriaCocina categoria;
    private String tareaEspecial;

    public static EmpleadoResponseDTO from(Empleado empleado) {
        EmpleadoResponseDTO dto = new EmpleadoResponseDTO();
        dto.setIdEmpleado(empleado.getIdEmpleado());
        dto.setNombre(empleado.getNombre());
        dto.setApellido(empleado.getApellido());
        dto.setDni(empleado.getDni());
        dto.setFechaNacimiento(empleado.getFechaNacimiento());
        dto.setIngreso(empleado.getIngreso());
        dto.setActivo(empleado.isActivo());

        if (empleado.getUser() != null) {
            dto.setEmail(empleado.getUser().getUsername());
        }

        if (empleado instanceof Cajero cajero) {
            dto.setTipoEmpleado("CAJERO");
            dto.setTurno(cajero.getTurno());
        } else if (empleado instanceof Cocinero cocinero) {
            dto.setTipoEmpleado("COCINERO");
            dto.setCategoria(cocinero.getCategoria());
            dto.setTareaEspecial(cocinero.getTareaEspecial());
        }

        return dto;
    }
}