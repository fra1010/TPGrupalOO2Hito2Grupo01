package com.unla.TPGrupalOO2Hito2Grupo01.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Costo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCosto;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costoSuperficies;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costoMontaje;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal plusElectricidad;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sueldoBase;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal plusAntiguedad;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal plusCocinero;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal plusAyudante;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal plusLavaplatos;

    @OneToOne(mappedBy = "costo")
    private Festival festival;

    public Costo(
            BigDecimal costoSuperficies,
            BigDecimal costoMontaje,
            BigDecimal plusElectricidad,
            BigDecimal sueldoBase,
            BigDecimal plusAntiguedad,
            BigDecimal plusCocinero,
            BigDecimal plusAyudante,
            BigDecimal plusLavaplatos) {

        this.costoSuperficies = costoSuperficies;
        this.costoMontaje = costoMontaje;
        this.plusElectricidad = plusElectricidad;
        this.sueldoBase = sueldoBase;
        this.plusAntiguedad = plusAntiguedad;
        this.plusCocinero = plusCocinero;
        this.plusAyudante = plusAyudante;
        this.plusLavaplatos = plusLavaplatos;
    }
}