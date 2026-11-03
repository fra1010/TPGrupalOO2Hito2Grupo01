package com.unla.TPGrupalOO2Hito2Grupo01.services;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.Map;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Festival;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;

public interface IFestivalService {

    List<Festival> getAll();

    List<Festival> getAllActive();

    Optional<Festival> findById(Integer idFestival);

    Festival insertOrUpdate(Festival festival);

    List<UnidadVenta> rankingUnidadesPorRecaudacion(
            Integer idFestival
    );

    Map<Integer, BigDecimal> recaudacionesPorFestival(
            Integer idFestival
    );
}