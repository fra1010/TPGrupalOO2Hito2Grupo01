package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.*;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository("unidadVentaRepository")
public interface IUnidadVentaRepository
        extends JpaRepository<UnidadVenta, Integer> {

    @Query("""
        SELECT u
        FROM UnidadVenta u
        WHERE u.responsable.username = :username
        """)
    UnidadVenta traerUnidadVentaResponsable(
            @Param("username") String username
    );

    @Query("""
        SELECT u
        FROM UnidadVenta u
        JOIN FETCH u.festival f
        JOIN FETCH f.costo
        WHERE u.responsable.username = :username
        """)
    UnidadVenta traerUnidadVentaConCostoPorResponsable(
            @Param("username") String username
    );

    @Query("""
        SELECT u
        FROM UnidadVenta u
        LEFT JOIN FETCH u.empleados
        LEFT JOIN FETCH u.festival
        WHERE u = :unidadVenta
        """)
    UnidadVenta traerUnidadVentaConEmpleados(
            @Param("unidadVenta") UnidadVenta unidadVenta
    );

    UnidadVenta findByIdUnidadVenta(int idUnidadVenta);

    UnidadVenta findByCodigo(String codigo);

    @Query("""
        SELECT u
        FROM UnidadVenta u
        LEFT JOIN FETCH u.festival f
        LEFT JOIN FETCH f.costo
        WHERE u.responsable.username = :username
        """)
    UnidadVenta traerUnidadVentaConFestival(
            @Param("username") String username
    );

    @Query("""
        SELECT new com.unla.TPGrupalOO2Hito2Grupo01.dtos.ReciboDetalladoDTO(

            uv.nombre,
            e.nombre,
            e.apellido,

            CASE
                WHEN TYPE(e) = Cocinero THEN 'COCINERO'
                WHEN TYPE(e) = Cajero THEN 'CAJERO'
            END,

            CASE
                WHEN TYPE(e) = Cocinero
                THEN str(TREAT(e AS Cocinero).categoria)
            END,

            greatest(f.fechaInicio, :desde),
            least(f.fechaFin, coalesce(:hasta, f.fechaFin)),

            cast(
                cast(
                    function(
                        'datediff',
                        least(f.fechaFin, coalesce(:hasta, f.fechaFin)),
                        greatest(f.fechaInicio, :desde)
                    ) as BigDecimal
                ) + 1
                as Long
            ),

            cast(
                CASE
                    WHEN cast(
                        function(
                            'datediff',
                            least(f.fechaFin, coalesce(:hasta, f.fechaFin)),
                            greatest(f.fechaInicio, :desde)
                        ) as BigDecimal
                    ) + 1 <= 15 THEN 1

                    WHEN cast(
                        function(
                            'datediff',
                            least(f.fechaFin, coalesce(:hasta, f.fechaFin)),
                            greatest(f.fechaInicio, :desde)
                        ) as BigDecimal
                    ) + 1 <= 30 THEN 2

                    ELSE floor(
                        (
                            cast(
                                function(
                                    'datediff',
                                    least(f.fechaFin, coalesce(:hasta, f.fechaFin)),
                                    greatest(f.fechaInicio, :desde)
                                ) as BigDecimal
                            ) + 1
                        ) / 15
                    ) + 1
                END
                as Long
            ),

            cast(
                (
                    CASE
                        WHEN cast(
                            function(
                                'datediff',
                                least(f.fechaFin, coalesce(:hasta, f.fechaFin)),
                                greatest(f.fechaInicio, :desde)
                            ) as BigDecimal
                        ) + 1 <= 15 THEN 1

                        WHEN cast(
                            function(
                                'datediff',
                                least(f.fechaFin, coalesce(:hasta, f.fechaFin)),
                                greatest(f.fechaInicio, :desde)
                            ) as BigDecimal
                        ) + 1 <= 30 THEN 2

                        ELSE floor(
                            (
                                cast(
                                    function(
                                        'datediff',
                                        least(f.fechaFin, coalesce(:hasta, f.fechaFin)),
                                        greatest(f.fechaInicio, :desde)
                                    ) as BigDecimal
                                ) + 1
                            ) / 15
                        ) + 1
                    END
                ) * cast(0.5 as BigDecimal)
                as BigDecimal
            ),

            c.sueldoBase,

            CASE
                WHEN TYPE(e) = Cocinero
                THEN 'Plus categoría ' || str(TREAT(e AS Cocinero).categoria)
                ELSE 'Plus antigüedad'
            END,

            cast(
                CASE
                    WHEN TYPE(e) = Cocinero THEN 1
                    ELSE TREAT(e AS Cajero).antiguedad
                END
                as Integer
            ),

            cast(
                CASE
                    WHEN TYPE(e) = Cocinero THEN
                        CASE TREAT(e AS Cocinero).categoria
                            WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                            WHEN 'COCINERO' THEN c.plusCocinero
                            WHEN 'LAVAPLATOS' THEN c.plusLavaplatos
                            ELSE cast(0 as BigDecimal)
                        END
                    ELSE c.plusAntiguedad
                END
                as BigDecimal
            ),

            cast(
                (
                    CASE
                        WHEN TYPE(e) = Cocinero THEN 1
                        ELSE TREAT(e AS Cajero).antiguedad
                    END
                ) * (
                    CASE
                        WHEN TYPE(e) = Cocinero THEN
                            CASE TREAT(e AS Cocinero).categoria
                                WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                                WHEN 'COCINERO' THEN c.plusCocinero
                                WHEN 'LAVAPLATOS' THEN c.plusLavaplatos
                                ELSE cast(0 as BigDecimal)
                            END
                        ELSE c.plusAntiguedad
                    END
                )
                as BigDecimal
            ),

            cast(
                c.sueldoBase
                + (
                    CASE
                        WHEN TYPE(e) = Cocinero THEN 1
                        ELSE TREAT(e AS Cajero).antiguedad
                    END
                ) * (
                    CASE
                        WHEN TYPE(e) = Cocinero THEN
                            CASE TREAT(e AS Cocinero).categoria
                                WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                                WHEN 'COCINERO' THEN c.plusCocinero
                                WHEN 'LAVAPLATOS' THEN c.plusLavaplatos
                                ELSE cast(0 as BigDecimal)
                            END
                        ELSE c.plusAntiguedad
                    END
                )
                as BigDecimal
            ),

            cast(
                (
                    CASE
                        WHEN cast(
                            function(
                                'datediff',
                                least(f.fechaFin, coalesce(:hasta, f.fechaFin)),
                                greatest(f.fechaInicio, :desde)
                            ) as BigDecimal
                        ) + 1 <= 15 THEN 1

                        WHEN cast(
                            function(
                                'datediff',
                                least(f.fechaFin, coalesce(:hasta, f.fechaFin)),
                                greatest(f.fechaInicio, :desde)
                            ) as BigDecimal
                        ) + 1 <= 30 THEN 2

                        ELSE floor(
                            (
                                cast(
                                    function(
                                        'datediff',
                                        least(f.fechaFin, coalesce(:hasta, f.fechaFin)),
                                        greatest(f.fechaInicio, :desde)
                                    ) as BigDecimal
                                ) + 1
                            ) / 15
                        ) + 1
                    END
                )
                * cast(0.5 as BigDecimal)
                * (
                    c.sueldoBase
                    + (
                        CASE
                            WHEN TYPE(e) = Cocinero THEN 1
                            ELSE TREAT(e AS Cajero).antiguedad
                        END
                    ) * (
                        CASE
                            WHEN TYPE(e) = Cocinero THEN
                                CASE TREAT(e AS Cocinero).categoria
                                    WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                                    WHEN 'COCINERO' THEN c.plusCocinero
                                    WHEN 'LAVAPLATOS' THEN c.plusLavaplatos
                                    ELSE cast(0 as BigDecimal)
                                END
                            ELSE c.plusAntiguedad
                        END
                    )
                )
                as BigDecimal
            )
        )
        FROM UnidadVenta uv
            JOIN uv.empleados e
            JOIN uv.festival f
            JOIN f.costo c
        WHERE uv = :unidadVenta
          AND TYPE(e) IN (Cocinero, Cajero)
          AND e.activo = true
          AND f.fechaInicio <= coalesce(:hasta, f.fechaFin)
          AND f.fechaFin >= :desde
        ORDER BY e.apellido, e.nombre
        """)
    List<ReciboDetalladoDTO> traerSueldoPorEmpleado(
            @Param("unidadVenta") UnidadVenta unidadVenta,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    @Query("""
        SELECT new com.unla.TPGrupalOO2Hito2Grupo01.dtos.ReciboDetalladoEmpleadoDTO(

            uv.nombre,
            e.nombre,
            e.apellido,

            CASE
                WHEN TYPE(e) = Cocinero THEN 'COCINERO'
                WHEN TYPE(e) = Cajero THEN 'CAJERO'
            END,

            CASE
                WHEN TYPE(e) = Cocinero
                THEN str(TREAT(e AS Cocinero).categoria)
            END,

            f.fechaInicio,
            f.fechaFin,

            cast(
                cast(
                    function('datediff', f.fechaFin, f.fechaInicio)
                    as BigDecimal
                ) + 1
                as Long
            ),

            cast(
                CASE
                    WHEN cast(
                        function('datediff', f.fechaFin, f.fechaInicio)
                        as BigDecimal
                    ) + 1 <= 15 THEN 1

                    WHEN cast(
                        function('datediff', f.fechaFin, f.fechaInicio)
                        as BigDecimal
                    ) + 1 <= 30 THEN 2

                    ELSE floor(
                        (
                            cast(
                                function('datediff', f.fechaFin, f.fechaInicio)
                                as BigDecimal
                            ) + 1
                        ) / 15
                    ) + 1
                END
                as Long
            ),

            cast(
                (
                    CASE
                        WHEN cast(
                            function('datediff', f.fechaFin, f.fechaInicio)
                            as BigDecimal
                        ) + 1 <= 15 THEN 1

                        WHEN cast(
                            function('datediff', f.fechaFin, f.fechaInicio)
                            as BigDecimal
                        ) + 1 <= 30 THEN 2

                        ELSE floor(
                            (
                                cast(
                                    function('datediff', f.fechaFin, f.fechaInicio)
                                    as BigDecimal
                                ) + 1
                            ) / 15
                        ) + 1
                    END
                ) * cast(0.5 as BigDecimal)
                as BigDecimal
            ),

            c.sueldoBase,

            CASE
                WHEN TYPE(e) = Cocinero
                THEN 'Plus categoría ' || str(TREAT(e AS Cocinero).categoria)
                ELSE 'Plus antigüedad'
            END,

            cast(
                CASE
                    WHEN TYPE(e) = Cocinero THEN 1
                    ELSE TREAT(e AS Cajero).antiguedad
                END
                as Integer
            ),

            cast(
                CASE
                    WHEN TYPE(e) = Cocinero THEN
                        CASE TREAT(e AS Cocinero).categoria
                            WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                            WHEN 'COCINERO' THEN c.plusCocinero
                            WHEN 'LAVAPLATOS' THEN c.plusLavaplatos
                            ELSE cast(0 as BigDecimal)
                        END
                    ELSE c.plusAntiguedad
                END
                as BigDecimal
            ),

            cast(
                (
                    CASE
                        WHEN TYPE(e) = Cocinero THEN 1
                        ELSE TREAT(e AS Cajero).antiguedad
                    END
                ) * (
                    CASE
                        WHEN TYPE(e) = Cocinero THEN
                            CASE TREAT(e AS Cocinero).categoria
                                WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                                WHEN 'COCINERO' THEN c.plusCocinero
                                WHEN 'LAVAPLATOS' THEN c.plusLavaplatos
                                ELSE cast(0 as BigDecimal)
                            END
                        ELSE c.plusAntiguedad
                    END
                )
                as BigDecimal
            ),

            cast(
                c.sueldoBase
                + (
                    CASE
                        WHEN TYPE(e) = Cocinero THEN 1
                        ELSE TREAT(e AS Cajero).antiguedad
                    END
                ) * (
                    CASE
                        WHEN TYPE(e) = Cocinero THEN
                            CASE TREAT(e AS Cocinero).categoria
                                WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                                WHEN 'COCINERO' THEN c.plusCocinero
                                WHEN 'LAVAPLATOS' THEN c.plusLavaplatos
                                ELSE cast(0 as BigDecimal)
                            END
                        ELSE c.plusAntiguedad
                    END
                )
                as BigDecimal
            ),

            cast(
                (
                    CASE
                        WHEN cast(
                            function('datediff', f.fechaFin, f.fechaInicio)
                            as BigDecimal
                        ) + 1 <= 15 THEN 1

                        WHEN cast(
                            function('datediff', f.fechaFin, f.fechaInicio)
                            as BigDecimal
                        ) + 1 <= 30 THEN 2

                        ELSE floor(
                            (
                                cast(
                                    function('datediff', f.fechaFin, f.fechaInicio)
                                    as BigDecimal
                                ) + 1
                            ) / 15
                        ) + 1
                    END
                )
                * cast(0.5 as BigDecimal)
                * (
                    c.sueldoBase
                    + (
                        CASE
                            WHEN TYPE(e) = Cocinero THEN 1
                            ELSE TREAT(e AS Cajero).antiguedad
                        END
                    ) * (
                        CASE
                            WHEN TYPE(e) = Cocinero THEN
                                CASE TREAT(e AS Cocinero).categoria
                                    WHEN 'COCINERO_AYUDANTE' THEN c.plusAyudante
                                    WHEN 'COCINERO' THEN c.plusCocinero
                                    WHEN 'LAVAPLATOS' THEN c.plusLavaplatos
                                    ELSE cast(0 as BigDecimal)
                                END
                            ELSE c.plusAntiguedad
                        END
                    )
                )
                as BigDecimal
            )
        )
        FROM UnidadVenta uv
            JOIN uv.empleados e
            JOIN uv.festival f
            JOIN f.costo c
        WHERE e.user.username = :username
          AND TYPE(e) IN (Cocinero, Cajero)
          AND e.activo = true
        """)
    ReciboDetalladoEmpleadoDTO traerSueldoPorUsername(
            @Param("username") String username
    );

    @Query("""
    SELECT u
    FROM UnidadVenta u
    JOIN u.empleados e
    WHERE e.user.username = :username
      AND TYPE(e) = Cajero
      AND e.activo = true
    """)
    UnidadVenta findByCajeroUsername(
            @Param("username") String username
    );

    @Query("""
        SELECT COALESCE(SUM(i.totalItem), 0)
        FROM UnidadVenta uv
        JOIN uv.pedidos p
        JOIN p.itemsPedido i
        WHERE uv = :unidad
        """)
    BigDecimal recaudacion(
            @Param("unidad") UnidadVenta unidad
    );

    @Query("""
        SELECT new com.unla.TPGrupalOO2Hito2Grupo01.dtos.PlatoReporteDTO(
            pl.idPlato,
            pl.nombre,
            SUM(i.cantidad),
            SUM(i.totalItem),
            pl.precioDeVenta
        )
        FROM UnidadVenta uv
        JOIN uv.pedidos p
        JOIN p.itemsPedido i
        JOIN i.plato pl
        WHERE uv = :unidad
        GROUP BY pl.idPlato, pl.nombre
        ORDER BY SUM(i.totalItem) DESC
        """)
    List<PlatoReporteDTO> detallePorPlato(
            @Param("unidad") UnidadVenta unidad
    );

    @Query("""
        SELECT uv.idUnidadVenta
        FROM UnidadVenta uv
        LEFT JOIN uv.pedidos p
        LEFT JOIN p.itemsPedido item
        WHERE uv.festival.idFestival = :idFestival
        GROUP BY uv.idUnidadVenta, uv.nombre
        ORDER BY COALESCE(SUM(item.totalItem), 0) DESC,
                 uv.nombre ASC,
                 uv.idUnidadVenta ASC
        """)
    List<Integer> rankingIdsPorRecaudacion(
            @Param("idFestival") Integer idFestival
    );

    @Query("""
        SELECT uv.idUnidadVenta,
               COALESCE(SUM(item.totalItem), cast(0 as BigDecimal))
        FROM UnidadVenta uv
        LEFT JOIN uv.pedidos p
        LEFT JOIN p.itemsPedido item
        WHERE uv.festival.idFestival = :idFestival
        GROUP BY uv.idUnidadVenta
        """)
    List<Object[]> recaudacionesPorFestival(
            @Param("idFestival") Integer idFestival
    );

    @Query("""
        SELECT u
        FROM UnidadVenta u
        JOIN FETCH u.festival f
        JOIN FETCH f.costo
        WHERE f.idFestival = :idFestival
        """)
    List<UnidadVenta> traerUnidadesConCostoPorFestival(
            @Param("idFestival") Integer idFestival
    );

    @Query("""
        SELECT COALESCE(SUM(i.totalItem), 0)
        FROM UnidadVenta uv
        JOIN uv.pedidos p
        JOIN p.itemsPedido i
        WHERE uv = :unidad
          AND p.fecha BETWEEN :desde AND :hasta
        """)
    BigDecimal ingresosRentabilidad(
            @Param("unidad") UnidadVenta unidad,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    @Query("""
        SELECT COALESCE(SUM(i.cantidad * pl.costoDePlato), 0)
        FROM UnidadVenta uv
        JOIN uv.pedidos p
        JOIN p.itemsPedido i
        JOIN i.plato pl
        WHERE uv = :unidad
          AND p.fecha BETWEEN :desde AND :hasta
        """)
    BigDecimal costosPlatosRentabilidad(
            @Param("unidad") UnidadVenta unidad,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );
}