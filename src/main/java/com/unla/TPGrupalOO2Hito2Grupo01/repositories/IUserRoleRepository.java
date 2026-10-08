package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.UserRole;

@Repository
public interface IUserRoleRepository extends JpaRepository<UserRole, Integer> {
}