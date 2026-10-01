package com.unla.TPGrupalOO2Hito2Grupo01.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Client extends Person {

	private String mail;
}
