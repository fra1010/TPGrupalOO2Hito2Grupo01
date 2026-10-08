package com.unla.TPGrupalOO2Hito2Grupo01.services;

import java.util.List;
import java.util.Optional;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Festival;

public interface IFestivalService {

    List<Festival> getAll();

    List<Festival> getAllActive();

    Optional<Festival> findById(Integer idFestival);

    Festival insertOrUpdate(Festival festival);
}