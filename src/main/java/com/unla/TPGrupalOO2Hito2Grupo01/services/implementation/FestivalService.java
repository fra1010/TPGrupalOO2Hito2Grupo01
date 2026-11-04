package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PlatoReporteDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Plato;
import java.util.List;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Festival;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IFestivalRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUnidadVentaRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IPedidoRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IFestivalService;

@Service("festivalService")
public class FestivalService implements IFestivalService {

    private final IFestivalRepository festivalRepository;
    private final IUnidadVentaRepository unidadVentaRepository;
    private final IPedidoRepository pedidoRepository;

    public FestivalService(
            IFestivalRepository festivalRepository,
            IUnidadVentaRepository unidadVentaRepository, IPedidoRepository pedidoRepository) {

        this.festivalRepository = festivalRepository;
        this.unidadVentaRepository = unidadVentaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public List<Festival> getAll() {
        return festivalRepository.findAll();
    }

    @Override
    public List<Festival> getAllActive() {
        return festivalRepository.findByActivoTrue();
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

    @Override
    @Transactional(readOnly = true)
    public List<UnidadVenta> rankingUnidadesPorRecaudacion(
            Integer idFestival) {

        if (idFestival == null) {
            throw new IllegalArgumentException(
                    "Debe indicar un festival."
            );
        }

        if (!festivalRepository.existsById(idFestival)) {
            throw new IllegalArgumentException(
                    "No se encontró el festival indicado."
            );
        }

        List<Integer> idsOrdenados =
                unidadVentaRepository.rankingIdsPorRecaudacion(
                        idFestival
                );

        if (idsOrdenados.isEmpty()) {
            return List.of();
        }

        Map<Integer, UnidadVenta> unidadesPorId =
                unidadVentaRepository.findAllById(idsOrdenados)
                        .stream()
                        .collect(Collectors.toMap(
                                UnidadVenta::getIdUnidadVenta,
                                Function.identity()
                        ));

        // Se reconstruye la lista siguiendo el orden del ranking.
        return idsOrdenados.stream()
                .map(unidadesPorId::get)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, BigDecimal> recaudacionesPorFestival(
            Integer idFestival) {

        if (idFestival == null) {
            throw new IllegalArgumentException(
                    "Debe indicar un festival."
            );
        }

        if (!festivalRepository.existsById(idFestival)) {
            throw new IllegalArgumentException(
                    "No se encontró el festival indicado."
            );
        }

        List<Object[]> resultados =
                unidadVentaRepository.recaudacionesPorFestival(
                        idFestival
                );

        Map<Integer, BigDecimal> recaudaciones = new HashMap<>();

        for (Object[] fila : resultados) {

            Integer idUnidadVenta = (Integer) fila[0];

            BigDecimal recaudacion = fila[1] == null
                    ? BigDecimal.ZERO
                    : (BigDecimal) fila[1];

            recaudaciones.put(idUnidadVenta, recaudacion);
        }

        return recaudaciones;
    }

    private void validarPeriodo(Festival festival) {

        if (festival.getFechaInicio() == null
                || festival.getFechaFin() == null) {

            throw new IllegalArgumentException(
                    "La fecha de inicio y la fecha de fin son obligatorias"
            );
        }

        if (festival.getFechaFin().isBefore(
                festival.getFechaInicio())) {

            throw new IllegalArgumentException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio"
            );
        }
    }

    @Override
    public PlatoReporteDTO obtenerPlatoEstrella(Integer idFestival, Integer idUnidad) {
        List<PlatoReporteDTO> platos = pedidoRepository.findPlatoEstrellaByFestivalAndUnidad(
                idFestival,
                idUnidad,
                PageRequest.of(0, 1)
        );

        if (platos.isEmpty()) {
            return null; // Devuelve null para que el controlador lo reciba y la vista muestre el mensaje
        }

        return platos.getFirst();
    }

}