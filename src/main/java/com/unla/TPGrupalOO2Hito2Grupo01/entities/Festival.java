package com.unla.TPGrupalOO2Hito2Grupo01.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFestival;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String temporada;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

//    @OneToMany(
//            mappedBy = "festival",
//            fetch = FetchType.LAZY
//    )
//    private Set<UnidadVenta> unidadesVenta = new HashSet<>();

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            optional = false
    )
    @JoinColumn(name = "costo_id", nullable = false) //festival es el lado dueño de la relacion
    private Costo costo;

    @Column(nullable = false)
    private boolean activo = true;

    public Festival(
            String nombre,
            String temporada,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Costo costo) {

        this.nombre = nombre;
        this.temporada = temporada;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.costo = costo;
    }
}