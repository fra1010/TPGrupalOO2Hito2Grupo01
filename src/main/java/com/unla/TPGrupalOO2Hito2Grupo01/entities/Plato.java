package com.unla.TPGrupalOO2Hito2Grupo01.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PROTECTED)
    private Integer idPlato;

    @NotBlank(message = "El nombre del plato es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "El precio de venta debe ser mayor a cero")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioDeVenta;

    @NotNull(message = "El costo de producción es obligatorio")
    @DecimalMin(value = "0.0",
            message = "El costo de producción no puede ser negativo")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costoDePlato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_venta", nullable = false)
    @NotNull(message = "El plato debe pertenecer a una unidad de venta")
    private UnidadVenta unidadVenta;

    @Column(nullable = false)
    private boolean activo = true;
}