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
public class Empleado
{
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


    // relacion 1 a 1 con usuario
    @OneToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}, // persiste
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "id_user", unique = true)

    protected User user;

   // relacion N a 1 con UnidadVenta (varios empleados en una unidad)

   @ManyToOne(fetch = FetchType.LAZY)

   @JoinColumn(name = "id_unidad_venta",insertable = false,updatable = false)
   protected UnidadVenta unidadVenta;

   // relacion 1 a N con liquidacion
  // @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//   protected Set<Liquidacion> liquidaciones;


    public Empleado(String nombre, String apellido, long dni, LocalDate fechaNacimiento, LocalDate ingreso, boolean activo)
    {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.ingreso = ingreso;
        this.activo = activo;

    }

    // metodos auxiliares para usar en thymeleaf de forma segura

    public boolean isCajero()
    {
        return this instanceof com.unla.TPGrupalOO2Hito2Grupo01.entities.Cajero;
    }

    public boolean isCocinero()
    {
        return this instanceof com.unla.TPGrupalOO2Hito2Grupo01.entities.Cocinero;
    }

    public String getTipo()
    {
        if (this instanceof com.unla.TPGrupalOO2Hito2Grupo01.entities.Cajero)
        {
            return "Cajero";
        }
        else if (this instanceof com.unla.TPGrupalOO2Hito2Grupo01.entities.Cocinero)
        {
            return "Cocinero";
        }

        return "Empleado";
    }

}