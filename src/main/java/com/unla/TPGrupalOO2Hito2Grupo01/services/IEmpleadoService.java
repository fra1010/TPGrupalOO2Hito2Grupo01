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

	// Alta indicando la unidad de venta directamente (usado por la API REST,
	// donde no hay un responsable logueado).
	Empleado darDeAltaEnUnidad(
			Empleado empleado,
			String email,
			Integer idUnidadVenta
	);

	Empleado cambiarEstadoEmpleado(
			Integer idEmpleado,
			boolean activo,
			String usernameResponsable
	);

	Empleado traerPorUsername(String username);

	// Filtros opcionales: cualquier parametro null (o vacio) no se aplica.
	List<Empleado> filtrar(
			Integer idUnidadVenta,
			String apellido,
			Boolean activo,
			String tipoEmpleado
	);
}