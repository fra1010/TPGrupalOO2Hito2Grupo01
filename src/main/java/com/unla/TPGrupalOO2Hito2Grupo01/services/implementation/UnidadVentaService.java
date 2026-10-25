package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.*;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.*;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUnidadVentaRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IUnidadVenta;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
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
    public ReporteCanonDTO calcularCanon(String username) {
        ReporteCanonDTO reporte = unidadVentaRepository.calcularCanonReporte(username);

        if (reporte == null) {
            throw new EntityNotFoundException(
                    "No se encontró una unidad de venta con festival y costos para: " + username);
        }
        if ("OTRO".equals(reporte.getTipo())) {
            throw new IllegalArgumentException("Tipo de unidad sin cálculo de canon.");
        }

        return reporte;
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
        if (unidad.getFestival() != null) {
            unidadVentaDTO.setIdFestival(unidad.getFestival().getIdFestival());
            unidadVentaDTO.setNombreFestival(unidad.getFestival().getNombre());
        }
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

    @Override
    public void agregarUnidadVenta(UnidadVentaFormDTO unidadVenta, UserDTO responsable) {

        UnidadVentaDTO unidad = switch (unidadVenta.getTipoUnidad()) {
            case UnidadVentaFormDTO.FOOD_TRUCK -> modelMapper.map(unidadVenta, FoodTruckDTO.class);
            case UnidadVentaFormDTO.PUESTO_DESARMABLE -> modelMapper.map(unidadVenta, PuestoDesarmableDTO.class);
            default -> throw new IllegalStateException(
                    "Tipo de unidad no soportado: " + unidadVenta.getTipoUnidad());
        };

        String codigo= generarCodigo(10);
        while (unidadVentaRepository.findByCodigo(codigo)!=null){
            codigo= generarCodigo(10);
        }
        if (unidad instanceof FoodTruckDTO foodTruckDTO) {

            UnidadVenta guardar = modelMapper.map(foodTruckDTO, FoodTruck.class);  // DTO -> ENTITY
            guardar.setResponsable(modelMapper.map(responsable, User.class));
            guardar.setCodigo(codigo);
            unidadVentaRepository.save(guardar);                                   // se guarda la entidad
        }
        else {
            if (unidad instanceof PuestoDesarmableDTO puestoDesarmableDTO){
                UnidadVenta guardar = modelMapper.map(puestoDesarmableDTO, PuestoDesarmable.class);  // DTO -> ENTITY
                guardar.setResponsable(modelMapper.map(responsable, User.class));
                guardar.setCodigo(codigo);
                unidadVentaRepository.save(guardar);
            }
        }

    }

    @Override
    public UnidadVenta traerUnidadVentaResponsable(String username) {
        return unidadVentaRepository.traerUnidadVentaResponsable(username);
    }

    private String generarCodigo(int longitud) {

        String caracteres =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        + "abcdefghijklmnopqrstuvwxyz"
                        + "0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(longitud);

        for (int i = 0; i < longitud; i++) {
            sb.append(
                    caracteres.charAt(
                            random.nextInt(caracteres.length())
                    )
            );
        }

        return sb.toString().toUpperCase();
    }
    }


