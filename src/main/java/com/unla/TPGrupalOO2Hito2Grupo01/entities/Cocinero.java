package com.unla.TPGrupalOO2Hito2Grupo01.entities;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cocinero")
@PrimaryKeyJoinColumn(name = "idEmpleado") // uno la clave de Cocinero con la de Empleado
@Getter
@Setter
@NoArgsConstructor

public class Cocinero extends Empleado
{
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaCocina categoria;

    @Column(length = 100)
    private String tareaEspecial;

    // constructor completo usando super() para inicializar la base comun

    public Cocinero(String nombre, String apellido, long dni, LocalDate fechaNacimiento, LocalDate ingreso,
                    boolean activo, CategoriaCocina categoria, String tareaEspecial)
    {
        super(nombre, apellido, dni, fechaNacimiento, ingreso, activo);
        this.categoria = categoria;
        this.tareaEspecial = tareaEspecial;
    }
}
