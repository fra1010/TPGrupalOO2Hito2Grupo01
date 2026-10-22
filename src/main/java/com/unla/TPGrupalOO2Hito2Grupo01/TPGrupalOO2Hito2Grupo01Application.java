package com.unla.TPGrupalOO2Hito2Grupo01;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.User;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUserRepository;

@SpringBootApplication
public class TPGrupalOO2Hito2Grupo01Application {

	public static void main(String[] args) {
		SpringApplication.run(TPGrupalOO2Hito2Grupo01Application.class, args);
	}

	@Bean
	public CommandLineRunner initData(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			System.out.println("=========================================");

			// 1. Buscamos el usuario 'empleado' usando el método de tu repositorio
			User empleado = userRepository.findByUsernameAndFetchUserRolesEagerly("empleado");

			if (empleado != null) {
				System.out.println(" USUARIO ENCONTRADO EN BASE DE DATOS.");

				// 2. Le inyectamos la clave 'admin' codificada nativamente por la app
				empleado.setPassword(passwordEncoder.encode("admin"));
				empleado.setEnabled(true);
				userRepository.save(empleado);

				System.out.println(" CONTRASEÑA ACTUALIZADA A 'admin' DESDE JAVA.");
				System.out.println("CANTIDAD DE ROLES ASOCIADOS: " + empleado.getUserRoles().size());
			} else {
				System.out.println(" ERROR: El método de búsqueda devolvio NULL para 'empleado'.");
			}

			System.out.println("=========================================");
		};
	}
}
