package com.unla.TPGrupalOO2Hito2Grupo01;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUserRepository;

@SpringBootApplication
public class TPGrupalOO2Hito2Grupo01Application {

	public static void main(String[] args) {
		SpringApplication.run(TPGrupalOO2Hito2Grupo01Application.class, args);
	}

}
