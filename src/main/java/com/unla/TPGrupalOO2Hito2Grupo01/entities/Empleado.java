package com.unla.TPGrupalOO2Hito2Grupo01.entities;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor

@Inheritance(strategy = InheritanceType.JOINED)
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer idEmpleado;

    @Column(nullable = false, length = 50)
    protected String nombre;

    @Column(nullable = false, length = 50)
    protected String apellido;

    @Column(nullable = false)
    protected long dni;

    @Column(nullable = false)
    protected LocalDate fechaNacimiento;

    @Column(nullable = false)
    protected LocalDate ingreso;

    @Column(nullable = false)
    protected boolean activo;

    // Relación 1 a 1 con User
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    protected User user;

    /* ----- relaciones comentadas -----

   // Relación N a 1 con UnidadVenta (Varios empleados en una unidad)
   @ManyToOne(fetch = FetchType.LAZY)

   @JoinColumn(name = "id_unidad_venta")
   protected UnidadVenta unidadVenta;

   // Relación 1 a N con Liquidacion
   @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   protected Set<Liquidacion> liquidaciones;

*/
    public Empleado(String nombre, String apellido, long dni,
                    LocalDate fechaNacimiento, LocalDate ingreso,
                    boolean activo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.ingreso = ingreso;
        this.activo = activo;

    }

    // Metodo para obtener la antigüedad calculada de forma dinámica
    public int getAntiguedad() {
        if (this.ingreso == null) return 0;
        return (int) ChronoUnit.YEARS.between(this.ingreso, LocalDate.now());
    }

}