package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.AuthorityUtils;

import com.unla.TPGrupalOO2Hito2Grupo01.helpers.ViewRouteHelper;

import java.util.Set;

@Controller
public class UserController {

	@GetMapping("/login")
	public String login(Model model,
						@RequestParam(required=false) String error,
						@RequestParam(required=false) String logout) {
		model.addAttribute("error", error);
		model.addAttribute("logout", logout);
		return ViewRouteHelper.USER_LOGIN;
	}

	@GetMapping("/logout")
	public String logout(Model model) {
		return ViewRouteHelper.USER_LOGOUT;
	}

	@GetMapping("/loginsuccess")
	public String loginCheck() {
		// 1. Capturamos los datos de la sesión actual
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// 2. Extraemos los roles asignados al usuario en un Set de Strings
		Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());

		// 3. Si el rol coincide con EMPLEADO, lo mandamos a su sección dedicada
		if (roles.contains("ROLE_EMPLEADO") || roles.contains("EMPLEADO")) {
			return ViewRouteHelper.EMPLEADO_ROOT; // Redirige a "redirect:/empleado"
		}

		// En cualquier otro caso (ADMIN o RESPONSABLE), va al inicio general del sitio
		return "redirect:" + ViewRouteHelper.ROUTE; // Redirige a "/index"
	}
}
