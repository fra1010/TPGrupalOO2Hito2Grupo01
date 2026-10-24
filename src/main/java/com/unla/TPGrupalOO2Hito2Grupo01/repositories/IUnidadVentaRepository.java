package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.SueldoEmpleadoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository("unidadVentaRepository")
public interface IUnidadVentaRepository extends JpaRepository<UnidadVenta, Integer> {
    @Query("SELECT u FROM UnidadVenta u WHERE u.responsable.username = :username")
    public abstract UnidadVenta traerUnidadVentaResponsable(@Param("username") String username );
    @Query("""
        SELECT CASE
            WHEN TYPE(u) = FoodTruck THEN
                (CAST(u.superficie AS BigDecimal) * c.costoSuperficies)
                + (CASE WHEN TREAT(u AS FoodTruck).conexion = true
                        THEN c.plusElectricidad ELSE 0bd END)
            WHEN TYPE(u) = PuestoDesarmable THEN
                (CAST(u.superficie AS BigDecimal) * c.costoSuperficies)
                + (TREAT(u AS PuestoDesarmable).tiempo * c.costoMontaje)
            ELSE 0bd
        END
        FROM UnidadVenta u
        JOIN u.festival f
        JOIN f.costo c
        WHERE u = :unidadVenta
        """)
    public BigDecimal calcularCanonUnidad(@Param("unidadVenta") UnidadVenta unidadVenta);

    @Query("SELECT u FROM UnidadVenta u LEFT JOIN FETCH u.empleados WHERE u = :unidadVenta")
    public UnidadVenta traerUnidadVentaConEmpleados(@Param("unidadVenta") UnidadVenta unidadVenta );


    public UnidadVenta findByIdUnidadVenta(int idUnidadVenta);
    @Query("""
    SELECT new com.unla.TPGrupalOO2Hito2Grupo01.dtos.SueldoEmpleadoDTO(
           uv.nombre,
           e.nombre,
           e.apellido,
           CASE
               WHEN TYPE(e) = Cocinero THEN 'COCINERO'
               WHEN TYPE(e) = Cajero   THEN 'CAJERO'
           END,
           CASE
               WHEN TYPE(e) = Cocinero THEN str(TREAT(e AS Cocinero).categoria)
           END,
           (floor((cast(function('datediff', f.fechaFin, f.fechaInicio) as BigDecimal) + 1) / 15) + 1)
           * cast(0.5 as BigDecimal)
           * (
               c.sueldoBase
               + CASE
                   WHEN TYPE(e) = Cocinero THEN
                       CASE TREAT(e AS Cocinero).categoria
                           WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                           WHEN 'COCINERO'          THEN c.plusCocinero
                           WHEN 'LAVAPLATOS'        THEN c.plusLavaplatos
                           ELSE cast(0 as BigDecimal)
                       END
                   WHEN TYPE(e) = Cajero THEN
                       TREAT(e AS Cajero).antiguedad * c.plusAntiguedad
                   ELSE cast(0 as BigDecimal)
                 END
           ))
    FROM UnidadVenta uv
        JOIN uv.empleados e
        JOIN uv.festival f
        JOIN f.costo c
    WHERE uv = :unidadVenta
      AND TYPE(e) IN (Cocinero, Cajero)
    ORDER BY e.apellido, e.nombre
    """)
    public List<SueldoEmpleadoDTO> traerSueldoPorEmpleado(@Param("unidadVenta") UnidadVenta unidadVenta);
    public UnidadVenta findByCodigo(String codigo);

}
