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

    //  verifico si ya exitia ese empleado

    Optional<Empleado> findByDni(long dni);

    //  Buscor empleado por el nombreusuario del usuario logueado

    @Query("SELECT e FROM Empleado e JOIN e.user u WHERE u.username = :username")
    Optional<Empleado> findByUsername(@Param("username") String username);

}
