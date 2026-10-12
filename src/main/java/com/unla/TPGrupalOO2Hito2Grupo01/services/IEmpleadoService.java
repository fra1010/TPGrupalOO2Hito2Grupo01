package com.unla.TPGrupalOO2Hito2Grupo01.services;

import java.util.List;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;

public interface IEmpleadoService {

	List<Empleado> getAll();

	List<Empleado> getAllActive();

	Empleado findById(Integer idEmpleado);

	Empleado insertOrUpdate(Empleado empleado);
}
