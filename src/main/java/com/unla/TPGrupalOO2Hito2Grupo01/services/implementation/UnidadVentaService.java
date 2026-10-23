package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.*;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.*;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUnidadVentaRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IUnidadVenta;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

@Service
public class UnidadVentaService implements IUnidadVenta {

    private final IUnidadVentaRepository unidadVentaRepository;
    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder pe =
            new BCryptPasswordEncoder();

    public UnidadVentaService(
            @Qualifier("modelMapperUnidadVenta") ModelMapper modelMapper,
            IUnidadVentaRepository unidadVentaRepository) {

        this.modelMapper = modelMapper;
        this.unidadVentaRepository = unidadVentaRepository;
    }

    @Override
    public List<UnidadVentaDTO> traerUnidadesVenta() {

        List<UnidadVentaDTO> unidades = new ArrayList<>();

        for (UnidadVenta unidadVenta : unidadVentaRepository.findAll()) {
            if (unidadVenta instanceof FoodTruck) {
                unidades.add(
                        modelMapper.map(
                                (FoodTruck) unidadVenta,
                                FoodTruckDTO.class
                        )
                );
            } else {
                unidades.add(
                        modelMapper.map(
                                (PuestoDesarmable) unidadVenta,
                                PuestoDesarmableDTO.class
                        )
                );
            }
        }

        return unidades;
    }

    @Override
    public void agregarOActualizarUnidadVenta(
            UnidadVentaDTO unidadVenta) {

        if (unidadVenta.getIdUnidadVenta() != null) {

            // Actualizar unidad existente.
            if (unidadVenta instanceof FoodTruckDTO foodTruckDTO) {
                unidadVentaRepository.save(
                        modelMapper.map(
                                foodTruckDTO,
                                FoodTruck.class
                        )
                );

            } else if (unidadVenta
                    instanceof PuestoDesarmableDTO puestoDTO) {

                unidadVentaRepository.save(
                        modelMapper.map(
                                puestoDTO,
                                PuestoDesarmable.class
                        )
                );
            }

        } else {

            // Agregar una nueva unidad.
            if (unidadVenta instanceof FoodTruckDTO foodTruckDTO) {

                UnidadVenta guardar = modelMapper.map(
                        foodTruckDTO,
                        FoodTruck.class
                );

                guardar.getResponsable().setPassword(
                        pe.encode(
                                unidadVenta.getResponsable().getPassword()
                        )
                );

                guardar.getResponsable().getUserRoles().add(
                        new UserRole(
                                guardar.getResponsable(),
                                Role.RESPONSABLE
                        )
                );

                unidadVentaRepository.save(guardar);

            } else if (unidadVenta
                    instanceof PuestoDesarmableDTO puestoDTO) {

                UnidadVenta guardar = modelMapper.map(
                        puestoDTO,
                        PuestoDesarmable.class
                );

                guardar.getResponsable().setPassword(
                        pe.encode(
                                unidadVenta.getResponsable().getPassword()
                        )
                );

                guardar.getResponsable().getUserRoles().add(
                        new UserRole(
                                guardar.getResponsable(),
                                Role.RESPONSABLE
                        )
                );

                unidadVentaRepository.save(guardar);
            }
        }
    }

    @Override
    public BigDecimal calcularCanon(String username) {

        UnidadVenta unidadVenta =
                unidadVentaRepository.traerUnidadVentaResponsable(
                        username
                );

        return unidadVentaRepository.calcularCanonUnidad(unidadVenta);
    }

    @Override
    @Transactional
    public UnidadVentaDTO traerUnidadVentaConEmpleados(
            String username) {

        UnidadVenta unidad =
                unidadVentaRepository.traerUnidadVentaResponsable(
                        username
                );

        if (unidad == null) {
            throw new IllegalArgumentException(
                    "El responsable no tiene una unidad de venta asignada."
            );
        }

        // Cargar la unidad con su colección de empleados.
        unidad = unidadVentaRepository.traerUnidadVentaConEmpleados(
                unidad
        );

        UnidadVentaDTO unidadVentaDTO = modelMapper.map(
                unidad,
                UnidadVentaDTO.class
        );

        List<EmpleadoDTO> empleadoDTOS = new ArrayList<>();

        // No filtrar por activo: el listado debe permitir
        // visualizar y activar a los empleados inactivos.
        for (Empleado empleado : unidad.getEmpleados()) {

            EmpleadoDTO dto = modelMapper.map(
                    empleado,
                    EmpleadoDTO.class
            );

            // Copiar explícitamente el estado real del empleado.
            dto.setActivo(empleado.isActivo());

            if (empleado instanceof Cajero) {

                Cajero cajero = (Cajero) empleado;

                dto.setTipoEmpleado("CAJERO");
                dto.setTurno(cajero.getTurno());

            } else if (empleado instanceof Cocinero) {

                Cocinero cocinero = (Cocinero) empleado;

                dto.setTipoEmpleado("COCINERO");
                dto.setCategoria(cocinero.getCategoria());
                dto.setTareaEspecial(cocinero.getTareaEspecial());
            }

            empleadoDTOS.add(dto);
        }

        // Mantiene el orden, sin importar el estado
        empleadoDTOS.sort(
                Comparator.comparing(EmpleadoDTO::getIdEmpleado)
        );

        unidadVentaDTO.setEmpleados(empleadoDTOS);

        return unidadVentaDTO;
    }

    @Override
    public List<SueldoEmpleadoDTO> calcularSueldoUnidadVenta(
            String username) {

        UnidadVenta unidad =
                unidadVentaRepository.traerUnidadVentaResponsable(
                        username
                );

        return unidadVentaRepository.traerSueldoPorEmpleado(unidad);
    }
}