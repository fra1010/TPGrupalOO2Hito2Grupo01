package com.unla.TPGrupalOO2Hito2Grupo01.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "food_truck")
@Getter
@Setter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id_unidad_venta")
public class FoodTruck extends UnidadVenta {
    @NotBlank(message = "La patente es obligatorio")
    private String patente;

    private boolean conexion;
}
