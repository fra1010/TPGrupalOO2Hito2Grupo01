package com.unla.TPGrupalOO2Hito2Grupo01.helpers;

public class ViewRouteHelper {

	/**** Views ****/

	// HOME
	public final static String INDEX = "home/index";

	// USER
	public final static String USER_LOGIN = "user/login";
	public final static String USER_LOGOUT = "user/logout";

	// EMPLEADO
	public final static String EMPLEADO_INDEX = "empleado/index";
	public final static String EMPLEADO_NEW = "empleado/new";
	public final static String EMPLEADO_MI_INFORMACION = "empleado/mi-informacion";

	/**** Redirects ****/

	public final static String ROUTE = "/index";

	public final static String EMPLEADO_ROOT = "redirect:/empleado";
}