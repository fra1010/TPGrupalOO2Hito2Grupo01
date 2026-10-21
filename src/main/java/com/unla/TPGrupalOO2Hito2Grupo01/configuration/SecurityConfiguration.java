
package com.unla.TPGrupalOO2Hito2Grupo01.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@SuppressWarnings("deprecation")
public class SecurityConfiguration {

	private final Environment environment;

	public SecurityConfiguration(Environment environment) {
		this.environment = environment;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)
			throws Exception {

		boolean perfilLocal = Arrays.asList(
				environment.getActiveProfiles()
		).contains("local");

		http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.disable())

				.authorizeHttpRequests(auth -> {
					auth.requestMatchers(
							"/login",
							"/loginprocess",
							"/css/**",
							"/imgs/**",
							"/js/**",
							"/vendor/**",
							"/api/v1/**"
					).permitAll();

					auth.requestMatchers(
							"/empleado/new",
							"/empleado/create",
							"/empleado/edit/**",
							"/empleado/delete/**"
					).hasAnyRole("ADMIN", "RESPONSABLE");

					auth.requestMatchers("/empleado/**")
							.hasAnyRole(
									"ADMIN",
									"RESPONSABLE",
									"EMPLEADO"
							);

					if (perfilLocal) {
						auth.requestMatchers("/empleado/**")
								.permitAll();
					}

					auth.anyRequest().authenticated();
				})

				.formLogin(login -> login
						.loginPage("/login")
						.loginProcessingUrl("/loginprocess")
						.usernameParameter("username")
						.passwordParameter("password")
						.defaultSuccessUrl("/loginsuccess", true)
						.failureUrl("/login?error=true")
						.permitAll()
				)

				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout=true")
						.permitAll()
				);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		// Temporalmente para desarrollo local.
		// La contraseña debe coincidir con el texto almacenado.
		return NoOpPasswordEncoder.getInstance();
	}
}
