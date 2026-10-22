
package com.unla.TPGrupalOO2Hito2Grupo01.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PROTECTED)
    private Integer idPedido;

    @NotNull(message = "La fecha del pedido es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull(message = "La unidad de venta es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_venta", nullable = false)
    private UnidadVenta unidadVenta;

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ItemPedido> itemsPedido = new HashSet<>();
}