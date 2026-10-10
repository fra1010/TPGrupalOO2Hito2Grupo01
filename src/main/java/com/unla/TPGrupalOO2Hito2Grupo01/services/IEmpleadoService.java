package com.unla.TPGrupalOO2Hito2Grupo01.services;

import java.util.List;
import java.util.Optional;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;

public interface IEmpleadoService {

    List<Empleado> getAll();

    List<Empleado> getAllActive(); 

    Optional<Empleado> findById(Integer idEmpleado);

    Empleado insertOrUpdate(Empleado empleado);
}
