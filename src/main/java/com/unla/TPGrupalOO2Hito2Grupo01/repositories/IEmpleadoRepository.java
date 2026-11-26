package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;
import org.springframework.data.repository.query.Param;

@Repository("empleadoRepository")
public interface IEmpleadoRepository extends JpaRepository<Empleado, Integer>
{
    @Query("SELECT e FROM Empleado e WHERE e.activo = true")
    List<Empleado> findByActivoTrue();

    @Query("SELECT e FROM Empleado e JOIN e.user u WHERE u.username = :username")
    Optional<Empleado> findByUsername(@Param("username") String username);


    // para verificar si ya exitia ese empleado
    Optional<Empleado> findByDni(long dni);


    @Query("""
        SELECT e
        FROM Empleado e
        LEFT JOIN e.unidadVenta u
        WHERE (:idUnidadVenta IS NULL OR u.idUnidadVenta = :idUnidadVenta)
          AND (:apellido IS NULL
               OR LOWER(e.apellido) LIKE LOWER(CONCAT('%', :apellido, '%')))
          AND (:activo IS NULL OR e.activo = :activo)
        ORDER BY e.apellido, e.nombre
        """)
    List<Empleado> filtrar(
            @Param("idUnidadVenta") Integer idUnidadVenta,
            @Param("apellido") String apellido,
            @Param("activo") Boolean activo
    );

}