
package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.unla.TPGrupalOO2Hito2Grupo01.helpers.ViewRouteHelper;

@Controller
public class UserController {

	@GetMapping("/login")
	public String login(
			Model model,
			@RequestParam(required = false) String error,
			@RequestParam(required = false) String logout) {

		model.addAttribute("error", error);
		model.addAttribute("logout", logout);

		return ViewRouteHelper.USER_LOGIN;
	}

	@GetMapping("/logout")
	public String logout() {
		// El cierre de sesión lo procesa Spring Security
		return "redirect:/login";
	}

	@GetMapping("/loginsuccess")
	public String loginCheck() {

		Authentication auth =
				SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated()) {
			return "redirect:/login";
		}

		Set<String> roles =
				AuthorityUtils.authorityListToSet(auth.getAuthorities());

		if (roles.contains("ROLE_ADMIN")
				|| roles.contains("ROLE_RESPONSABLE")
				|| roles.contains("ROLE_EMPLEADO")) {
			return "redirect:/index";
		}

		return "redirect:/login?error=true";
	}
}
