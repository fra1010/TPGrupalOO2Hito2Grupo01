
package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Plato;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IPlatoRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IPlatoService;

@Service("platoService")
public class PlatoService implements IPlatoService {

    private final IPlatoRepository platoRepository;

    public PlatoService(IPlatoRepository platoRepository) {
        this.platoRepository = platoRepository;
    }

    @Override
    public List<Plato> getAll() {
        return platoRepository.findAll();
    }

    @Override
    public List<Plato> getAllActive() {
        return platoRepository.findByActivoTrue();
    }

    @Override
    public Optional<Plato> findById(Integer idPlato) {
        return platoRepository.findById(idPlato);
    }

    @Override
    public Plato insertOrUpdate(Plato plato) {
        validarPlato(plato);
        return platoRepository.save(plato);
    }

    private void validarPlato(Plato plato) {

        if (plato == null) {
            throw new IllegalArgumentException(
                    "El plato no puede ser nulo");
        }

        if (plato.getNombre() == null ||
                plato.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del plato es obligatorio");
        }

        if (plato.getPrecioDeVenta() == null ||
                plato.getPrecioDeVenta()
                        .compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El precio de venta debe ser mayor a cero");
        }

        if (plato.getCostoDePlato() == null ||
                plato.getCostoDePlato()
                        .compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El costo de producción no puede ser negativo");
        }

        if (plato.getUnidadVenta() == null) {
            throw new IllegalArgumentException(
                    "El plato debe pertenecer a una unidad de venta");
        }
    }
}