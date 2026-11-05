package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.PlatoFormDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Plato;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IPlatoRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUnidadVentaRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IPlatoService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service("platoService")
public class PlatoService implements IPlatoService {

    private static final BigDecimal LIMITE_IMPORTE =
            new BigDecimal("10000000000");

    private final IPlatoRepository platoRepository;
    private final IUnidadVentaRepository unidadVentaRepository;

    public PlatoService(
            IPlatoRepository platoRepository,
            IUnidadVentaRepository unidadVentaRepository) {

        this.platoRepository = platoRepository;
        this.unidadVentaRepository = unidadVentaRepository;
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
    @Transactional
    public Plato insertOrUpdate(Plato plato) {
        validarPlato(plato);
        return platoRepository.save(plato);
    }

    @Override
    public List<Plato> getActivosByUnidad(
            Integer idUnidadVenta) {

        return platoRepository
                .findByUnidadVenta_IdUnidadVentaAndActivoTrue(
                        idUnidadVenta
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Plato> getActivosDelResponsable(
            String username) {

        UnidadVenta unidad =
                obtenerUnidadDelResponsable(username);

        return platoRepository
                .findByUnidadVenta_IdUnidadVentaAndActivoTrue(
                        unidad.getIdUnidadVenta()
                );
    }

    @Override
    @Transactional
    public Plato registrarPlato(
            PlatoFormDTO formulario,
            String username) {

        if (formulario == null) {
            throw new IllegalArgumentException(
                    "Los datos del plato son obligatorios."
            );
        }

        UnidadVenta unidad =
                obtenerUnidadDelResponsable(username);

        Plato plato = new Plato();

        if (formulario.getNombre() != null) {
            plato.setNombre(
                    formulario.getNombre().trim()
            );
        }

        plato.setPrecioDeVenta(
                formulario.getPrecioDeVenta()
        );

        plato.setCostoDePlato(
                formulario.getCostoDePlato()
        );

        plato.setUnidadVenta(unidad);
        plato.setActivo(true);

        validarPlato(plato);

        return platoRepository.save(plato);
    }

    private UnidadVenta obtenerUnidadDelResponsable(
            String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "No se pudo identificar al responsable."
            );
        }

        UnidadVenta unidad =
                unidadVentaRepository
                        .traerUnidadVentaResponsable(username);

        if (unidad == null) {
            throw new IllegalArgumentException(
                    "No tenés una unidad de venta asignada."
            );
        }

        return unidad;
    }

    private void validarPlato(Plato plato) {

        if (plato == null) {
            throw new IllegalArgumentException(
                    "El plato no puede ser nulo."
            );
        }

        if (plato.getNombre() == null
                || plato.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre del plato es obligatorio."
            );
        }

        if (plato.getNombre().length() > 255) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar los 255 caracteres."
            );
        }

        if (plato.getPrecioDeVenta() == null
                || plato.getPrecioDeVenta()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El precio de venta debe ser mayor a cero."
            );
        }

        if (plato.getCostoDePlato() == null
                || plato.getCostoDePlato()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El costo de producción no puede ser negativo."
            );
        }

        validarFormatoImporte(
                plato.getPrecioDeVenta(),
                "El precio de venta"
        );

        validarFormatoImporte(
                plato.getCostoDePlato(),
                "El costo de producción"
        );

        if (plato.getUnidadVenta() == null
                || plato.getUnidadVenta()
                .getIdUnidadVenta() == null) {

            throw new IllegalArgumentException(
                    "El plato debe pertenecer a una unidad de venta."
            );
        }
    }

    private void validarFormatoImporte(
            BigDecimal importe,
            String etiqueta) {

        if (importe.compareTo(LIMITE_IMPORTE) >= 0) {
            throw new IllegalArgumentException(
                    etiqueta
                            + " admite hasta 10 dígitos enteros."
            );
        }

        if (importe.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException(
                    etiqueta
                            + " admite hasta 2 decimales."
            );
        }
    }
}