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

import java.time.temporal.ChronoUnit;
import jakarta.transaction.Transactional;
import java.text.Normalizer;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
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

        UnidadVenta unidad =
                unidadVentaRepository.traerUnidadVentaConCostoPorResponsable(username);

        if (unidad == null) {
            throw new EntityNotFoundException(
                    "No se encontró una unidad de venta para: " + username);
        }

        Costo costo = unidad.getFestival().getCosto();

        BigDecimal superficie = BigDecimal.valueOf(unidad.getSuperficie());
        BigDecimal costoSuperficie =
                superficie.multiply(costo.getCostoSuperficies());

        String tipoUnidad = "OTRO";
        Integer conexionElectrica = 0;
        BigDecimal plusElectricidad = BigDecimal.ZERO;
        BigDecimal tiempoMontaje = BigDecimal.ZERO;
        BigDecimal costoMontaje = BigDecimal.ZERO;
        BigDecimal canon = costoSuperficie;

        if (unidad instanceof FoodTruck foodTruck) {
            tipoUnidad = "FOOD_TRUCK";

            if (foodTruck.isConexion()) {
                conexionElectrica = 1;
                plusElectricidad = costo.getPlusElectricidad();
                canon = canon.add(plusElectricidad);
            }

        } else if (unidad instanceof PuestoDesarmable puestoDesarmable) {
            tipoUnidad = "PUESTO_DESARMABLE";

            tiempoMontaje = BigDecimal.valueOf(puestoDesarmable.getTiempo());
            costoMontaje = tiempoMontaje.multiply(costo.getCostoMontaje());
            canon = canon.subtract(costoMontaje);
        }

        return new ReporteCanonDTO(
                unidad.getIdUnidadVenta(),
                tipoUnidad,
                unidad.getNombre(),
                unidad.getFestival().getNombre(),
                superficie,
                costo.getCostoSuperficies(),
                costoSuperficie,
                conexionElectrica,
                plusElectricidad,
                tiempoMontaje,
                costo.getCostoMontaje(),
                costoMontaje,
                canon
        );
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
    public List<ReciboDetalladoDTO>  calcularSueldoUnidadVenta(
            String username, LocalDate desde, LocalDate hasta) {
        UnidadVenta unidadVenta = unidadVentaRepository.traerUnidadVentaResponsable(username);
        if (unidadVenta == null) {
            throw new IllegalArgumentException(
                    "El responsable no tiene una unidad de venta asignada."
            );
        }
            return unidadVentaRepository.traerSueldoPorEmpleado(unidadVenta,desde,hasta);
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
    public UnidadVentaDTO traerUnidadVentaResponsable(String username) {
        UnidadVenta unidadVenta = unidadVentaRepository.traerUnidadVentaResponsable(username);
        UnidadVentaDTO dto=null;
        if (unidadVenta instanceof FoodTruck foodTruck) {

            dto = modelMapper.map(foodTruck,FoodTruckDTO.class );
            return dto;
        }
        else {
            if (unidadVenta instanceof PuestoDesarmable puestoDesarmable){
                dto = modelMapper.map(puestoDesarmable, PuestoDesarmableDTO.class);
                return dto;
            }
        }
        return dto;
    }

    @Override
    public ReciboDetalladoEmpleadoDTO sueldoEmpleado(String username) {
        return unidadVentaRepository.traerSueldoPorUsername(username);
    }

    @Override
    public UnidadVenta findByCajeroUsername(String username) {
        return unidadVentaRepository.findByCajeroUsername(username);
    }

    @Override
    public ReporteUnidadDTO reporteUnidadVenta(String username) {
        UnidadVenta unidad = unidadVentaRepository.traerUnidadVentaConFestival(username);

        return new ReporteUnidadDTO(
                unidad != null ? unidad.getFestival().getIdFestival() : null,
                unidad != null ? unidad.getFestival().getNombre() : null,
                unidad.getIdUnidadVenta(),
                unidad.getNombre(),
                unidadVentaRepository.recaudacion(unidad),
                unidadVentaRepository.detallePorPlato(unidad));
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

    @Override
    public List<UnidadVentaDTO> traerUnidadesPorFestival(Integer idFestival) {
        return unidadVentaRepository.traerUnidadesConCostoPorFestival(idFestival).stream()
                .map(unidad -> {
                    if (unidad instanceof FoodTruck foodTruck) {
                        return (UnidadVentaDTO) modelMapper.map(
                                foodTruck, FoodTruckDTO.class);
                    }
                    return (UnidadVentaDTO) modelMapper.map(
                            unidad, PuestoDesarmableDTO.class);
                })
                .toList();
    }

    @Override
    public List<ReporteMayoresCanonDTO> unidadesConMayorCanon(Integer idFestival, int x) {
        if (x <= 0) {
            throw new IllegalArgumentException("x debe ser mayor a 0");
        }

        return unidadVentaRepository.traerUnidadesConCostoPorFestival(idFestival).stream()
                .map(this::armarReporteCanon)
                .sorted(Comparator
                        .comparing(ReporteMayoresCanonDTO::getCanon).reversed()
                        .thenComparing(ReporteMayoresCanonDTO::getNombreComercial))
                .limit(x)
                .toList();
    }

    private ReporteMayoresCanonDTO armarReporteCanon(UnidadVenta u) {
        Costo costo = u.getFestival().getCosto();

        BigDecimal canon = BigDecimal.valueOf(u.getSuperficie())
                .multiply(costo.getCostoSuperficies());

        String tipoUnidad = "OTRO";

        if (u instanceof FoodTruck foodTruck) {
            tipoUnidad = "FOOD_TRUCK";

            if (foodTruck.isConexion()) {
                canon = canon.add(costo.getPlusElectricidad());
            }
        } else if (u instanceof PuestoDesarmable puestoDesarmable) {
            tipoUnidad = "PUESTO_DESARMABLE";

            BigDecimal costoMontaje =
                    BigDecimal.valueOf(puestoDesarmable.getTiempo())
                            .multiply(costo.getCostoMontaje());

            canon = canon.subtract(costoMontaje);
        }

        return new ReporteMayoresCanonDTO(
                u.getNombre(),
                u.getCodigo(),
                tipoUnidad,
                canon
        );
    }

    @Override
    public List<EmpleadoDTO> filtrarEmpleados(
            String username,
            List<String> campos,
            List<String> valores) {

        if (campos == null || valores == null
                || campos.size() != valores.size()) {
            throw new IllegalArgumentException(
                    "Los criterios de búsqueda no son válidos."
            );
        }

        for (int i = 0; i < campos.size(); i++) {
            validarCriterioEmpleado(campos.get(i), valores.get(i));
        }

        UnidadVentaDTO unidad = traerUnidadVentaConEmpleados(username);

        return unidad.getEmpleados().stream()
                .filter(EmpleadoDTO::isActivo)
                .filter(empleado -> {
                    for (int i = 0; i < campos.size(); i++) {
                        String valor = valores.get(i).trim();

                        if (!valor.isEmpty()
                                && !coincideCriterioEmpleado(
                                empleado,
                                campos.get(i),
                                valor)) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();
    }

    private void validarCriterioEmpleado(String campo, String valor) {

        if (campo == null || valor == null) {
            throw new IllegalArgumentException(
                    "Los criterios de búsqueda no son válidos."
            );
        }

        switch (campo) {
            case "nombreCompleto", "nombre", "apellido", "dni",
                 "fechaNacimiento", "ingreso", "tipoEmpleado", "turno",
                 "categoria", "tareaEspecial", "edadExacta",
                 "edadMinima", "edadMaxima" -> {
            }

            default -> throw new IllegalArgumentException(
                    "El atributo de búsqueda no es válido."
            );
        }

        String texto = valor.trim();

        if (texto.isEmpty()) {
            return;
        }

        try {
            switch (campo) {
                case "dni" -> {
                    long dni = Long.parseLong(texto);

                    if (dni <= 0) {
                        throw new IllegalArgumentException();
                    }
                }

                case "edadExacta", "edadMinima", "edadMaxima" -> {
                    int edad = Integer.parseInt(texto);

                    if (edad < 0) {
                        throw new IllegalArgumentException();
                    }
                }

                case "fechaNacimiento", "ingreso" ->
                        LocalDate.parse(texto);

                case "tipoEmpleado" -> {
                    if (!"CAJERO".equals(texto)
                            && !"COCINERO".equals(texto)) {
                        throw new IllegalArgumentException();
                    }
                }

                case "turno" -> Turno.valueOf(texto);

                case "categoria" -> CategoriaCocina.valueOf(texto);

                default -> {
                }
            }

        } catch (NumberFormatException e) {
            String mensaje = "dni".equals(campo)
                    ? "El DNI debe ser un número entero positivo."
                    : "La edad debe ser un número entero mayor o igual a cero.";

            throw new IllegalArgumentException(mensaje);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "La fecha ingresada no es válida."
            );

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "El valor ingresado para '" + campo + "' no es válido."
            );
        }
    }

    private boolean coincideCriterioEmpleado(
            EmpleadoDTO empleado,
            String campo,
            String valor) {

        return switch (campo) {

            case "nombreCompleto" ->
                    normalizar(
                            empleado.getNombre() + " " + empleado.getApellido()
                    ).contains(normalizar(valor));

            case "nombre" ->
                    contieneTextoEmpleado(empleado.getNombre(), valor);

            case "apellido" ->
                    contieneTextoEmpleado(empleado.getApellido(), valor);

            case "dni" ->
                    Long.toString(empleado.getDni()).startsWith(valor);

            case "fechaNacimiento" ->
                    LocalDate.parse(valor).equals(
                            empleado.getFechaNacimiento()
                    );

            case "ingreso" ->
                    LocalDate.parse(valor).equals(empleado.getIngreso());

            case "tipoEmpleado" ->
                    valor.equals(empleado.getTipoEmpleado());

            case "turno" ->
                    "CAJERO".equals(empleado.getTipoEmpleado())
                            && empleado.getTurno() == Turno.valueOf(valor);

            case "categoria" ->
                    "COCINERO".equals(empleado.getTipoEmpleado())
                            && empleado.getCategoria()
                            == CategoriaCocina.valueOf(valor);

            case "tareaEspecial" ->
                    "COCINERO".equals(empleado.getTipoEmpleado())
                            && contieneTextoEmpleado(
                            empleado.getTareaEspecial(),
                            valor
                    );

            case "edadExacta", "edadMinima", "edadMaxima" ->
                    coincideEdadEmpleado(empleado, campo, valor);

            default -> false;
        };
    }

    private boolean contieneTextoEmpleado(String texto, String valor) {
        return normalizar(texto).contains(normalizar(valor));
    }

    private boolean coincideEdadEmpleado(
            EmpleadoDTO empleado,
            String campo,
            String valor) {

        LocalDate nacimiento = empleado.getFechaNacimiento();
        LocalDate hoy = LocalDate.now();

        if (nacimiento == null || nacimiento.isAfter(hoy)) {
            return false;
        }

        int edad = Period.between(nacimiento, hoy).getYears();
        int edadBuscada = Integer.parseInt(valor);

        return switch (campo) {
            case "edadExacta" -> edad == edadBuscada;
            case "edadMinima" -> edad >= edadBuscada;
            case "edadMaxima" -> edad <= edadBuscada;
            default -> false;
        };
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim();
    }

    @Override
    @Transactional
    public ReporteRentabilidadDTO calcularRentabilidad(
            String username,
            LocalDate desde,
            LocalDate hasta) {

        UnidadVenta unidad =
                unidadVentaRepository.traerUnidadVentaConFestival(username);

        if (unidad == null) {
            throw new IllegalArgumentException(
                    "El responsable no tiene una unidad de venta asignada."
            );
        }

        Festival festival = unidad.getFestival();

        if (festival == null || festival.getCosto() == null) {
            throw new IllegalArgumentException(
                    "La unidad debe tener un festival con costos configurados."
            );
        }

        LocalDate inicioFestival = festival.getFechaInicio();
        LocalDate finFestival = festival.getFechaFin();

        if (inicioFestival == null || finFestival == null
                || inicioFestival.isAfter(finFestival)) {
            throw new IllegalArgumentException(
                    "Las fechas del festival no son válidas."
            );
        }

        LocalDate desdeSolicitado =
                desde != null ? desde : inicioFestival;

        LocalDate hastaSolicitado =
                hasta != null ? hasta : finFestival;

        if (desdeSolicitado.isAfter(hastaSolicitado)) {
            throw new IllegalArgumentException(
                    "La fecha 'Desde' no puede ser posterior a 'Hasta'."
            );
        }

        LocalDate desdeEfectivo = desdeSolicitado.isBefore(inicioFestival)
                ? inicioFestival
                : desdeSolicitado;

        LocalDate hastaEfectivo = hastaSolicitado.isAfter(finFestival)
                ? finFestival
                : hastaSolicitado;

        if (desdeEfectivo.isAfter(hastaEfectivo)) {
            throw new IllegalArgumentException(
                    "El período indicado no coincide con las fechas del festival."
            );
        }

        long diasFestival =
                ChronoUnit.DAYS.between(inicioFestival, finFestival) + 1;

        long diasPeriodo =
                ChronoUnit.DAYS.between(desdeEfectivo, hastaEfectivo) + 1;

        BigDecimal ingresos =
                unidadVentaRepository.ingresosRentabilidad(
                        unidad,
                        desdeEfectivo,
                        hastaEfectivo
                ).setScale(2, RoundingMode.HALF_UP);

        BigDecimal costosPlatos =
                unidadVentaRepository.costosPlatosRentabilidad(
                        unidad,
                        desdeEfectivo,
                        hastaEfectivo
                ).setScale(2, RoundingMode.HALF_UP);

        BigDecimal sueldos =
                unidadVentaRepository.traerSueldoPorEmpleado(
                                unidad,
                                desdeEfectivo,
                                hastaEfectivo
                        )
                        .stream()
                        .map(ReciboDetalladoDTO::getTotalAPagar)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal canonTotal = armarReporteCanon(unidad)
                .getCanon()
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal canonPeriodo = canonTotal
                .multiply(BigDecimal.valueOf(diasPeriodo))
                .divide(
                        BigDecimal.valueOf(diasFestival),
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal rentabilidad = ingresos
                .subtract(costosPlatos)
                .subtract(sueldos)
                .subtract(canonPeriodo)
                .setScale(2, RoundingMode.HALF_UP);

        return new ReporteRentabilidadDTO(
                unidad.getIdUnidadVenta(),
                unidad.getNombre(),
                festival.getNombre(),
                desdeEfectivo,
                hastaEfectivo,
                diasPeriodo,
                diasFestival,
                ingresos,
                costosPlatos,
                sueldos,
                canonTotal,
                canonPeriodo,
                rentabilidad
        );
    }

}


