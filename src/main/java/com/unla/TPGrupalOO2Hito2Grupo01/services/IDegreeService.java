package com.unla.TPGrupalOO2Hito2Grupo01.services;
import java.util.List;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Degree;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.DegreeDTO;


@SuppressWarnings("unused")
public interface IDegreeService {

	List<Degree> getAll();

	void insertOrUpdate(DegreeDTO degreeDTO);

	boolean remove(int id);
}

