package com.unla.TPGrupalOO2Hito2Grupo01.services;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PlatoFormDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Plato;

import java.util.List;
import java.util.Optional;

public interface IPlatoService {

    List<Plato> getAll();

    List<Plato> getAllActive();

    Optional<Plato> findById(Integer idPlato);

    Plato insertOrUpdate(Plato plato);

    List<Plato> getActivosByUnidad(Integer idUnidadVenta);

    List<Plato> getActivosDelResponsable(String username);

    Plato registrarPlato(
            PlatoFormDTO formulario,
            String username
    );
}