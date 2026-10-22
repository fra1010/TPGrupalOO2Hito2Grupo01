package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPlatoRepository extends JpaRepository<Plato, Integer> {

    List<Plato> findByActivoTrue();

    List<Plato> findByUnidadVenta_IdUnidadVentaAndActivoTrue(
            Integer idUnidadVenta
    );
}