package com.unla.TPGrupalOO2Hito2Grupo01.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Arrays;

import com.unla.TPGrupalOO2Hito2Grupo01.services.implementation.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

	private final Environment environment;

	public SecurityConfiguration(Environment environment) {
		this.environment = environment;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.disable())
				.authorizeHttpRequests(auth -> {
					auth.requestMatchers("/css/**", "/imgs/**", "/js/**", "/vendor/bootstrap/css/**",
							"/vendor/jquery/**", "/vendor/bootstrap/js/**", "/api/v1/**").permitAll();

					// Si el perfil local está activo, libera la URL de pruebas automáticamente
					if (Arrays.asList(environment.getActiveProfiles()).contains("local")) {
						auth.requestMatchers("/empleado/**").permitAll();
					}

					auth.anyRequest().authenticated();
				})
				.formLogin(login -> {
					login.loginPage("/login");
					login.loginProcessingUrl("/loginprocess");
					login.usernameParameter("username");
					login.passwordParameter("password");
					login.defaultSuccessUrl("/loginsuccess", true); // Fuerza la redirección al UserController
					login.permitAll();
				})
				.logout(logout -> {
					logout.logoutUrl("/logout");
					logout.logoutSuccessUrl("/login");
					logout.permitAll();
				})
				.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
