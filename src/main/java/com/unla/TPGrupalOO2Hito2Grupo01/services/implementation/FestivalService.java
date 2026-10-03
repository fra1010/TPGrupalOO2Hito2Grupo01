package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Festival;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IFestivalRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IFestivalService;

@Service("festivalService")
public class FestivalService implements IFestivalService {

    private final IFestivalRepository festivalRepository;

    public FestivalService(IFestivalRepository festivalRepository) {
        this.festivalRepository = festivalRepository;
    }

    @Override
    public List<Festival> getAll() {
        return festivalRepository.findAll();
    }

    @Override
    public Optional<Festival> findById(Integer idFestival) {
        return festivalRepository.findById(idFestival);
    }

    @Override
    public Festival insertOrUpdate(Festival festival) {
        validarPeriodo(festival);
        return festivalRepository.save(festival);
    }

    private void validarPeriodo(Festival festival) {
        if (festival.getFechaInicio() == null || festival.getFechaFin() == null) {
            throw new IllegalArgumentException(
                    "La fecha de inicio y la fecha de fin son obligatorias");
        }

        if (festival.getFechaFin().isBefore(festival.getFechaInicio())) {
            throw new IllegalArgumentException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }
}