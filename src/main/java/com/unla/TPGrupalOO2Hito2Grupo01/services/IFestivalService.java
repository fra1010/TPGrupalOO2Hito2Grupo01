package com.unla.TPGrupalOO2Hito2Grupo01.services;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Festival;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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