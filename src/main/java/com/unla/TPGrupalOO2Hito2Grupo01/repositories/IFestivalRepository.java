package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Festival;

import java.util.List;

@Repository
public interface IFestivalRepository extends JpaRepository<Festival, Integer> { //sin public la  interfaz es solo visible dentro de paquete repositories
    List<Festival> findByActivoTrue();
}