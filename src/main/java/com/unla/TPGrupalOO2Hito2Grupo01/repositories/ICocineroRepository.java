package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cocinero;

@Repository("cocineroRepository")
public interface ICocineroRepository extends JpaRepository<Cocinero, Integer> {
    // Spring Boot ya te da todos los métodos heredados para el Cocinero aquí
}
