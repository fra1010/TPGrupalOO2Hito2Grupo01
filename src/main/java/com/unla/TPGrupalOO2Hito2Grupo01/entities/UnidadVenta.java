package com.unla.TPGrupalOO2Hito2Grupo01.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "unidad_venta")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UnidadVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PROTECTED)
    protected Integer idUnidadVenta;

    @NotBlank(message = "El nombre es obligatorio")
    protected String nombre;

    @NotNull
    @Positive(message = "La superficie de carpas tiene que ser positivo")
    protected double superficie;

    @ManyToOne
    @JoinColumn(name = "id_festival")
    protected Festival festival;

    @NotBlank(message = "El codigo es obligatorio")
    @Column(name="codigo", nullable=false, length=45)
    protected String codigo;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id",unique = true, nullable = false)
    protected User responsable;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_venta")
    protected Set<Empleado> empleados=new HashSet<>();

//    @OneToMany(fetch = FetchType.LAZY, mappedBy ="unidadVenta")
//    protected Set<Pedido> pedidos=new HashSet<>();

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "unidadVenta")
//    protected Set<Plato> platos=new HashSet<>();

    @CreationTimestamp
    protected LocalDate fechaCreacion;

    @UpdateTimestamp
    protected LocalDate fechaModificacion;


}
