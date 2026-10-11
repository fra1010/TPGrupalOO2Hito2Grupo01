package com.unla.TPGrupalOO2Hito2Grupo01.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "puesto_desarmable")
@Getter
@Setter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id_unidad_venta")
public class PuestoDesarmable extends UnidadVenta {
    @NotNull
    @Positive(message = "La cantidad de carpas tiene que ser positivo")
    private int cantidadCarpas;
    @NotNull
    @Positive(message = "tiempo tiene que ser positivo")
    private int tiempo;
}
