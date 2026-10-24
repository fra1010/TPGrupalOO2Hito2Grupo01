package com.unla.TPGrupalOO2Hito2Grupo01.services;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.UserDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Role;

public interface IUser {
    public UserDTO agregarUser(UserDTO user,Role role);
}
