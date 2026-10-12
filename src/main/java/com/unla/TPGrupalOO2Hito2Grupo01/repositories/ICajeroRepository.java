package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cajero;

@Repository("cajeroRepository")
public interface ICajeroRepository extends JpaRepository<Cajero, Integer>
{

}
