package com.unla.TPGrupalOO2Hito2Grupo01.controllers;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.unla.TPGrupalOO2Hito2Grupo01.helpers.ViewRouteHelper;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IFestivalService;

@Controller
@RequestMapping("/")
public class HomeController {

	private final IFestivalService festivalService;

	public HomeController(IFestivalService festivalService) {
		this.festivalService = festivalService;
	}

	@GetMapping("/index")
	public ModelAndView index() {

		ModelAndView modelAndView =
				new ModelAndView(ViewRouteHelper.INDEX);

		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();

		Object principal = null;

		if (authentication != null) {
			principal = authentication.getPrincipal();
		}

		if (principal instanceof User user) {
			Set<String> roles =
					AuthorityUtils.authorityListToSet(
							user.getAuthorities()
					);

			if (roles.contains("ROLE_RESPONSABLE")) {
				modelAndView.setViewName("responsable/index");
			}

			modelAndView.addObject("username", user.getUsername());

		} else {
			modelAndView.addObject("username", "anonymous");
		}

		modelAndView.addObject(
				"festivales",
				festivalService.getAllActive()
		);

		return modelAndView;
	}

	@GetMapping("/")
	public RedirectView redirectToHomeIndex() {
		return new RedirectView(ViewRouteHelper.ROUTE);
	}
}