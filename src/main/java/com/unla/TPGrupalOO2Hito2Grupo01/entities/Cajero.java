package com.unla.TPGrupalOO2Hito2Grupo01.entities;


import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cajero")
@PrimaryKeyJoinColumn(name = "idEmpleado") // Une la clave de Cajero con la de Empleado
@Getter
@Setter
@NoArgsConstructor
public class Cajero extends Empleado {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Turno turno;
    private int antiguedad;
    /* ----- relaciones comentadas -----
    @OneToMany(mappedBy = "cajero", fetch = FetchType.LAZY)
    private Set<CierreCaja> cierresCaja;
    */

    // Constructor completo usando super() para inicializar la base
    public Cajero(String nombre, String apellido, long dni,
                  LocalDate fechaNacimiento, LocalDate ingreso,
                  boolean activo, Turno turno) {
        super(nombre, apellido, dni, fechaNacimiento, ingreso, activo);
        this.turno = turno;
    }
}
