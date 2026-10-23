package com.unla.TPGrupalOO2Hito2Grupo01.services;

import java.util.List;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;

public interface IEmpleadoService {

	List<Empleado> getAll();

	List<Empleado> getAllActive();

	Empleado findById(Integer idEmpleado);

	Empleado insertOrUpdate(Empleado empleado);

	Empleado activarEmpleado(
			Integer idEmpleado,
			String usernameResponsable
	);

	boolean deleteLogico(int idEmpleado);

	Empleado darDeAltaConUsuario(
			Empleado empleado,
			String email,
			String usernameResponsable
	);

	Empleado cambiarEstadoEmpleado(
			Integer idEmpleado,
			boolean activo,
			String usernameResponsable
	);
}