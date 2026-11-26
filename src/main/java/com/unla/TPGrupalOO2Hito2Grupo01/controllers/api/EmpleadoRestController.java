package com.unla.TPGrupalOO2Hito2Grupo01.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.EmpleadoRequestDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.EmpleadoResponseDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cajero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cocinero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IEmpleadoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/empleados")
@Tag(name = "Empleados",
        description = "Alta y consulta de empleados de las unidades de venta")
public class EmpleadoRestController {

    private final IEmpleadoService empleadoService;

    public EmpleadoRestController(IEmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Dar de alta un empleado",
            description = "Crea un cajero o cocinero activo en la unidad de venta indicada, "
                    + "genera su usuario (el email) y le envía por mail una contraseña "
                    + "aleatoria. Si el mail no se puede enviar, el alta no se realiza.")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Empleado creado",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EmpleadoResponseDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Datos inválidos, DNI o email ya registrados, "
                            + "unidad inexistente o fallo en el envío del mail",
                    content = @Content)
    })
    public ResponseEntity<EmpleadoResponseDTO> crearEmpleado(
            @Valid @RequestBody EmpleadoRequestDTO request) {

        Empleado empleado = crearEmpleadoDesdeDTO(request);

        Empleado guardado = empleadoService.darDeAltaEnUnidad(
                empleado,
                request.getEmail(),
                request.getIdUnidadVenta()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(EmpleadoResponseDTO.from(guardado));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Listar empleados con filtros",
            description = "Todos los filtros son opcionales y se pueden combinar. "
                    + "Sin filtros devuelve todos los empleados, activos e inactivos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Listado de empleados (puede estar vacío)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = EmpleadoResponseDTO.class)))),
            @ApiResponse(responseCode = "400",
                    description = "Parámetro inválido (por ejemplo, un tipo de empleado inexistente)",
                    content = @Content)
    })
    public ResponseEntity<List<EmpleadoResponseDTO>> listarEmpleados(
            @Parameter(description = "Id de la unidad de venta", example = "1")
            @RequestParam(name = "idUnidadVenta", required = false)
            Integer idUnidadVenta,

            @Parameter(description = "Apellido (coincidencia parcial, sin distinguir mayúsculas)",
                    example = "sal")
            @RequestParam(name = "apellido", required = false)
            String apellido,

            @Parameter(description = "true: solo activos, false: solo inactivos", example = "true")
            @RequestParam(name = "activo", required = false)
            Boolean activo,

            @Parameter(description = "Tipo de empleado: CAJERO o COCINERO", example = "CAJERO")
            @RequestParam(name = "tipoEmpleado", required = false)
            String tipoEmpleado) {

        List<EmpleadoResponseDTO> empleados = empleadoService
                .filtrar(idUnidadVenta, apellido, activo, tipoEmpleado)
                .stream()
                .map(EmpleadoResponseDTO::from)
                .toList();

        return ResponseEntity.ok(empleados);
    }

    // Arma la entidad segun el tipo elegido. Las validaciones del DTO
    // garantizan que turno / categoria vengan cuando corresponde.
    private Empleado crearEmpleadoDesdeDTO(EmpleadoRequestDTO dto) {

        if ("CAJERO".equalsIgnoreCase(dto.getTipoEmpleado())) {
            return new Cajero(
                    dto.getNombre(),
                    dto.getApellido(),
                    dto.getDni(),
                    dto.getFechaNacimiento(),
                    dto.getIngreso(),
                    true,
                    dto.getTurno()
            );
        }

        return new Cocinero(
                dto.getNombre(),
                dto.getApellido(),
                dto.getDni(),
                dto.getFechaNacimiento(),
                dto.getIngreso(),
                true,
                dto.getCategoria(),
                dto.getTareaEspecial()
        );
    }
}