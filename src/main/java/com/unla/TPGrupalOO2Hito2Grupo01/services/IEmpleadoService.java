package com.unla.TPGrupalOO2Hito2Grupo01.services;

import java.util.List;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;

public interface IEmpleadoService {

	List<Empleado> getAll();

	List<Empleado> getAllActive();

	Empleado findById(Integer idEmpleado);

	Empleado insertOrUpdate(Empleado empleado);

	// reactivar un empleado eliminado logicamente

	Empleado reactivarEmpleadoEliminado(Empleado existente, Empleado datosNuevos, String email);

	public boolean deleteLogico(int idEmpleado);

	Empleado darDeAltaConUsuario(Empleado empleado, String email);



}
