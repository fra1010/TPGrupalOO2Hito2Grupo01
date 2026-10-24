package com.unla.TPGrupalOO2Hito2Grupo01.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnidadVentaFormDTO extends UnidadVentaDTO{
    // Valores del <select> del formulario. Usarlos en lugar de strings sueltos.
    public static final String FOOD_TRUCK = "FOOD_TRUCK";
    public static final String PUESTO_DESARMABLE = "PUESTO_DESARMABLE";

    @NotBlank(message = "Debe seleccionar el tipo de unidad de venta")
    private String tipoUnidad; // FOOD_TRUCK o PUESTO_DESARMABLE

    // ----- Solo FOOD_TRUCK -----
    private String patente;
    private boolean conexion;

    // ----- Solo PUESTO_DESARMABLE -----
    // Wrappers (Integer): llegan null cuando la sección está oculta en el formulario.
    private Integer cantidadCarpas;
    private Integer tiempo;

    // ------------------------------------------------------------------
    // Validaciones que dependen del tipo.
    // Cada error queda asociado a la propiedad que da nombre al método
    // (patenteValida, cantidadCarpasValida, etc.): el formulario los muestra
    // con th:errors="*{patenteValida}", igual que mayorDeEdad en empleado.
    // ------------------------------------------------------------------

    // Sin tipo, el error ya lo informa @NotBlank; con tipo inventado, falla acá
    @AssertTrue(message = "El tipo de unidad seleccionado no es válido")
    public boolean isTipoUnidadValido() {
        if (tipoUnidad == null || tipoUnidad.isBlank()) {
            return true;
        }
        return FOOD_TRUCK.equals(tipoUnidad) || PUESTO_DESARMABLE.equals(tipoUnidad);
    }

    @AssertTrue(message = "La patente es obligatoria")
    public boolean isPatenteValida() {
        if (!FOOD_TRUCK.equals(tipoUnidad)) {
            return true; // No aplica a este tipo
        }
        return patente != null && !patente.isBlank();
    }

    @AssertTrue(message = "La cantidad de carpas tiene que ser positiva")
    public boolean isCantidadCarpasValida() {
        if (!PUESTO_DESARMABLE.equals(tipoUnidad)) {
            return true; // No aplica a este tipo
        }
        return cantidadCarpas != null && cantidadCarpas > 0;
    }

    @AssertTrue(message = "El tiempo tiene que ser positivo")
    public boolean isTiempoValido() {
        if (!PUESTO_DESARMABLE.equals(tipoUnidad)) {
            return true; // No aplica a este tipo
        }
        return tiempo != null && tiempo > 0;
    }

    // ------------------------------------------------------------------
    // Conversión a los DTOs específicos que ya usa la capa de servicio
    // ------------------------------------------------------------------

    public FoodTruckDTO toFoodTruckDTO() {
        FoodTruckDTO dto = new FoodTruckDTO();
        copiarCamposComunes(dto);
        dto.setPatente(patente);
        dto.setConexion(conexion);
        return dto;
    }

    public PuestoDesarmableDTO toPuestoDesarmableDTO() {
        PuestoDesarmableDTO dto = new PuestoDesarmableDTO();
        copiarCamposComunes(dto);
        // El controller ya validó que no sean null antes de llamar a este método.
        dto.setCantidadCarpas(cantidadCarpas);
        dto.setTiempo(tiempo);
        return dto;
    }

    private void copiarCamposComunes(UnidadVentaDTO destino) {
        destino.setIdUnidadVenta(getIdUnidadVenta());
        destino.setNombre(getNombre());
        destino.setSuperficie(getSuperficie());
        destino.setIdFestival(getIdFestival());
        destino.setCodigo(getCodigo());
        destino.setResponsable(getResponsable());
        destino.setEmpleados(getEmpleados());
    }
}
