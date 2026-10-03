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

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Costo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCosto;

    @Column(nullable = false)
    private double costoSuperficies;

    @Column(nullable = false)
    private double costoMontaje;

    @Column(nullable = false)
    private double plusElectricidad;

    @Column(nullable = false)
    private double sueldoBase;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToOne(mappedBy = "costo")
    private Festival festival;

    public Costo(
            double costoSuperficies,
            double costoMontaje,
            double plusElectricidad,
            double sueldoBase) {

        this.costoSuperficies = costoSuperficies;
        this.costoMontaje = costoMontaje;
        this.plusElectricidad = plusElectricidad;
        this.sueldoBase = sueldoBase;
    }
}