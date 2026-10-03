package com.unla.TPGrupalOO2Hito2Grupo01.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "user_role",
		uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"})) // la unica restriccion es user_id, impide que un usuario tenga mas de un rol
public class UserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY) //sigue con many to one para seguir siendo compatible con User
	@JoinColumn(name="user_id", nullable=false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 20)
	private Role role;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public UserRole(User user, Role role) { // se saco id porque la genera la db
		this.user = user;
		this.role = role;
	}
}
