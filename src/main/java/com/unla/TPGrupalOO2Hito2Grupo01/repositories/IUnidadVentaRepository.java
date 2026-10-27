package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.ReciboDetalladoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.ReciboDetalladoEmpleadoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.ReporteCanonDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.SueldoEmpleadoDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository("unidadVentaRepository")
public interface IUnidadVentaRepository extends JpaRepository<UnidadVenta, Integer> {
    @Query("SELECT u FROM UnidadVenta u WHERE u.responsable.username = :username")
    public abstract UnidadVenta traerUnidadVentaResponsable(@Param("username") String username );
    @Query("""
    SELECT new com.unla.TPGrupalOO2Hito2Grupo01.dtos.ReporteCanonDTO(
        u.id,
        CASE WHEN TYPE(u) = FoodTruck THEN 'FOOD_TRUCK'
             WHEN TYPE(u) = PuestoDesarmable THEN 'PUESTO_DESARMABLE'
             ELSE 'OTRO' END,
        u.nombre,
        f.nombre,
        CAST(u.superficie AS BigDecimal),
        c.costoSuperficies,
        (CAST(u.superficie AS BigDecimal) * c.costoSuperficies),

        CASE WHEN TYPE(u) = FoodTruck THEN
                        (CASE WHEN TREAT(u AS FoodTruck).conexion = true THEN 1 ELSE 0 END)
                     ELSE 0 END ,
        CASE WHEN TYPE(u) = FoodTruck THEN
                (CASE WHEN TREAT(u AS FoodTruck).conexion = true
                      THEN c.plusElectricidad ELSE 0bd END)
             ELSE 0bd END,

        CASE WHEN TYPE(u) = PuestoDesarmable
             THEN CAST(TREAT(u AS PuestoDesarmable).tiempo AS BigDecimal)
             ELSE 0bd END,
        c.costoMontaje,
        CASE WHEN TYPE(u) = PuestoDesarmable
             THEN (TREAT(u AS PuestoDesarmable).tiempo * c.costoMontaje)
             ELSE 0bd END,

        CASE
            WHEN TYPE(u) = FoodTruck THEN
                (CAST(u.superficie AS BigDecimal) * c.costoSuperficies)
                + (CASE WHEN TREAT(u AS FoodTruck).conexion = true
                        THEN c.plusElectricidad ELSE 0bd END)
            WHEN TYPE(u) = PuestoDesarmable THEN
                (CAST(u.superficie AS BigDecimal) * c.costoSuperficies)
                + (TREAT(u AS PuestoDesarmable).tiempo * c.costoMontaje)
            ELSE 0bd
        END
    )
    FROM UnidadVenta u
    JOIN u.festival f
    JOIN f.costo c
    WHERE u.responsable.username = :username
    """)
    public ReporteCanonDTO calcularCanonReporte(@Param("username") String username);

    @Query("SELECT u FROM UnidadVenta u LEFT JOIN FETCH u.empleados LEFT JOIN FETCH u.festival WHERE u = :unidadVenta")
    public UnidadVenta traerUnidadVentaConEmpleados(@Param("unidadVenta") UnidadVenta unidadVenta );


    public UnidadVenta findByIdUnidadVenta(int idUnidadVenta);
//    @Query("""
//    SELECT new com.unla.TPGrupalOO2Hito2Grupo01.dtos.SueldoEmpleadoDTO(
//           uv.nombre,
//           e.nombre,
//           e.apellido,
//           CASE
//               WHEN TYPE(e) = Cocinero THEN 'COCINERO'
//               WHEN TYPE(e) = Cajero   THEN 'CAJERO'
//           END,
//           CASE
//               WHEN TYPE(e) = Cocinero THEN str(TREAT(e AS Cocinero).categoria)
//           END,
//           (floor((cast(function('datediff', f.fechaFin, f.fechaInicio) as BigDecimal) + 1) / 15) + 1)
//           * cast(0.5 as BigDecimal)
//           * (
//               c.sueldoBase
//               + CASE
//                   WHEN TYPE(e) = Cocinero THEN
//                       CASE TREAT(e AS Cocinero).categoria
//                           WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
//                           WHEN 'COCINERO'          THEN c.plusCocinero
//                           WHEN 'LAVAPLATOS'        THEN c.plusLavaplatos
//                           ELSE cast(0 as BigDecimal)
//                       END
//                   WHEN TYPE(e) = Cajero THEN
//                       TREAT(e AS Cajero).antiguedad * c.plusAntiguedad
//                   ELSE cast(0 as BigDecimal)
//                 END
//           ))
//    FROM UnidadVenta uv
//        JOIN uv.empleados e
//        JOIN uv.festival f
//        JOIN f.costo c
//    WHERE uv = :unidadVenta
//      AND TYPE(e) IN (Cocinero, Cajero)
//    ORDER BY e.apellido, e.nombre
//    """)
//    public List<SueldoEmpleadoDTO> traerSueldoPorEmpleado(@Param("unidadVenta") UnidadVenta unidadVenta);
    public UnidadVenta findByCodigo(String codigo);
    @Query("""
    SELECT u
    FROM UnidadVenta u
    LEFT JOIN FETCH u.festival f
    LEFT JOIN FETCH f.costo
    WHERE u.responsable.username = :username
    """)
    UnidadVenta traerUnidadVentaConFestival(@Param("username") String username);
    @Query("""
    SELECT new com.unla.TPGrupalOO2Hito2Grupo01.dtos.ReciboDetalladoDTO(
 
           uv.nombre,
           e.nombre,
           e.apellido,
           CASE WHEN TYPE(e) = Cocinero THEN 'COCINERO'
                WHEN TYPE(e) = Cajero   THEN 'CAJERO' END,
           CASE WHEN TYPE(e) = Cocinero THEN str(TREAT(e AS Cocinero).categoria) END,
 
           greatest(f.fechaInicio, :desde),
           least(f.fechaFin, coalesce(:hasta, f.fechaFin)),
 
           cast(cast(function('datediff', least(f.fechaFin, coalesce(:hasta, f.fechaFin)), greatest(f.fechaInicio, :desde)) as BigDecimal) + 1 as Long),
 
           cast(floor((cast(function('datediff', least(f.fechaFin, coalesce(:hasta, f.fechaFin)), greatest(f.fechaInicio, :desde)) as BigDecimal) + 1) / 15) + 1 as Long),
 
           cast((floor((cast(function('datediff', least(f.fechaFin, coalesce(:hasta, f.fechaFin)), greatest(f.fechaInicio, :desde)) as BigDecimal) + 1) / 15) + 1)
                * cast(0.5 as BigDecimal) as BigDecimal),
 
           c.sueldoBase,
 
           CASE WHEN TYPE(e) = Cocinero THEN 'Plus categoría ' || str(TREAT(e AS Cocinero).categoria)
                ELSE 'Plus antigüedad' END,
 
           cast(CASE WHEN TYPE(e) = Cocinero THEN 1
                     ELSE TREAT(e AS Cajero).antiguedad END as Integer),
 
           cast(CASE WHEN TYPE(e) = Cocinero THEN
                    CASE TREAT(e AS Cocinero).categoria
                        WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                        WHEN 'COCINERO'          THEN c.plusCocinero
                        WHEN 'LAVAPLATOS'        THEN c.plusLavaplatos
                        ELSE cast(0 as BigDecimal)
                    END
                ELSE c.plusAntiguedad END as BigDecimal),
 
           cast((CASE WHEN TYPE(e) = Cocinero THEN 1
                      ELSE TREAT(e AS Cajero).antiguedad END)
                * (CASE WHEN TYPE(e) = Cocinero THEN
                        CASE TREAT(e AS Cocinero).categoria
                            WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                            WHEN 'COCINERO'          THEN c.plusCocinero
                            WHEN 'LAVAPLATOS'        THEN c.plusLavaplatos
                            ELSE cast(0 as BigDecimal)
                        END
                    ELSE c.plusAntiguedad END) as BigDecimal),
 
           cast(c.sueldoBase
                + (CASE WHEN TYPE(e) = Cocinero THEN 1
                        ELSE TREAT(e AS Cajero).antiguedad END)
                * (CASE WHEN TYPE(e) = Cocinero THEN
                        CASE TREAT(e AS Cocinero).categoria
                            WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                            WHEN 'COCINERO'          THEN c.plusCocinero
                            WHEN 'LAVAPLATOS'        THEN c.plusLavaplatos
                            ELSE cast(0 as BigDecimal)
                        END
                    ELSE c.plusAntiguedad END) as BigDecimal),
 
           cast((floor((cast(function('datediff', least(f.fechaFin, coalesce(:hasta, f.fechaFin)), greatest(f.fechaInicio, :desde)) as BigDecimal) + 1) / 15) + 1)
                * cast(0.5 as BigDecimal)
                * (c.sueldoBase
                   + (CASE WHEN TYPE(e) = Cocinero THEN 1
                           ELSE TREAT(e AS Cajero).antiguedad END)
                   * (CASE WHEN TYPE(e) = Cocinero THEN
                           CASE TREAT(e AS Cocinero).categoria
                               WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                               WHEN 'COCINERO'          THEN c.plusCocinero
                               WHEN 'LAVAPLATOS'        THEN c.plusLavaplatos
                               ELSE cast(0 as BigDecimal)
                           END
                       ELSE c.plusAntiguedad END)) as BigDecimal)
    )
    FROM UnidadVenta uv
        JOIN uv.empleados e
        JOIN uv.festival f
        JOIN f.costo c
    WHERE uv = :unidadVenta
      AND TYPE(e) IN (Cocinero, Cajero)
      AND e.activo = true
      AND f.fechaInicio <= coalesce(:hasta, f.fechaFin)
      AND f.fechaFin    >= :desde
    ORDER BY e.apellido, e.nombre
    """)
    List<ReciboDetalladoDTO> traerSueldoPorEmpleado(@Param("unidadVenta") UnidadVenta unidadVenta,
                                                  @Param("desde") LocalDate desde,
                                                  @Param("hasta") LocalDate hasta);
    @Query("""
SELECT new com.unla.TPGrupalOO2Hito2Grupo01.dtos.ReciboDetalladoEmpleadoDTO(

       uv.nombre,
       e.nombre,
       e.apellido,
       CASE WHEN TYPE(e) = Cocinero THEN 'COCINERO'
            WHEN TYPE(e) = Cajero   THEN 'CAJERO' END,
       CASE WHEN TYPE(e) = Cocinero THEN str(TREAT(e AS Cocinero).categoria) END,

       f.fechaInicio,
       f.fechaFin,

       cast(cast(function('datediff', f.fechaFin, f.fechaInicio) as BigDecimal) + 1 as Long),

       cast(floor((cast(function('datediff', f.fechaFin, f.fechaInicio) as BigDecimal) + 1) / 15) + 1 as Long),

       cast((floor((cast(function('datediff', f.fechaFin, f.fechaInicio) as BigDecimal) + 1) / 15) + 1)
            * cast(0.5 as BigDecimal) as BigDecimal),

       c.sueldoBase,

       CASE WHEN TYPE(e) = Cocinero THEN 'Plus categoría ' || str(TREAT(e AS Cocinero).categoria)
            ELSE 'Plus antigüedad' END,

       cast(CASE WHEN TYPE(e) = Cocinero THEN 1
                 ELSE TREAT(e AS Cajero).antiguedad END as Integer),

       cast(CASE WHEN TYPE(e) = Cocinero THEN
                CASE TREAT(e AS Cocinero).categoria
                    WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                    WHEN 'COCINERO'          THEN c.plusCocinero
                    WHEN 'LAVAPLATOS'        THEN c.plusLavaplatos
                    ELSE cast(0 as BigDecimal)
                END
            ELSE c.plusAntiguedad END as BigDecimal),

       cast((CASE WHEN TYPE(e) = Cocinero THEN 1
                  ELSE TREAT(e AS Cajero).antiguedad END)
            * (CASE WHEN TYPE(e) = Cocinero THEN
                    CASE TREAT(e AS Cocinero).categoria
                        WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                        WHEN 'COCINERO'          THEN c.plusCocinero
                        WHEN 'LAVAPLATOS'        THEN c.plusLavaplatos
                        ELSE cast(0 as BigDecimal)
                    END
                ELSE c.plusAntiguedad END) as BigDecimal),

       cast(c.sueldoBase
            + (CASE WHEN TYPE(e) = Cocinero THEN 1
                    ELSE TREAT(e AS Cajero).antiguedad END)
            * (CASE WHEN TYPE(e) = Cocinero THEN
                    CASE TREAT(e AS Cocinero).categoria
                        WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                        WHEN 'COCINERO'          THEN c.plusCocinero
                        WHEN 'LAVAPLATOS'        THEN c.plusLavaplatos
                        ELSE cast(0 as BigDecimal)
                    END
                ELSE c.plusAntiguedad END) as BigDecimal),

       cast((floor((cast(function('datediff', f.fechaFin, f.fechaInicio) as BigDecimal) + 1) / 15) + 1)
            * cast(0.5 as BigDecimal)
            * (c.sueldoBase
               + (CASE WHEN TYPE(e) = Cocinero THEN 1
                       ELSE TREAT(e AS Cajero).antiguedad END)
               * (CASE WHEN TYPE(e) = Cocinero THEN
                       CASE TREAT(e AS Cocinero).categoria
                           WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                           WHEN 'COCINERO'          THEN c.plusCocinero
                           WHEN 'LAVAPLATOS'        THEN c.plusLavaplatos
                           ELSE cast(0 as BigDecimal)
                       END
                   ELSE c.plusAntiguedad END)) as BigDecimal)
)
FROM UnidadVenta uv
    JOIN uv.empleados e
    JOIN uv.festival f
    JOIN f.costo c
WHERE e.user.username = :username
  AND TYPE(e) IN (Cocinero, Cajero)
  AND e.activo = true
""")
    public ReciboDetalladoEmpleadoDTO traerSueldoPorUsername(@Param("username") String username);

}

