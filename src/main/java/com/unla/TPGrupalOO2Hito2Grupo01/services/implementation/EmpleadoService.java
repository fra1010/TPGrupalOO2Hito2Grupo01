package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cajero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cocinero;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IEmpleadoRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.ICajeroRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.ICocineroRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IEmpleadoService;

@Service("empleadoService")
public class EmpleadoService implements IEmpleadoService {

    @Autowired
    @Qualifier("empleadoRepository")
    private IEmpleadoRepository empleadoRepository;

    @Autowired
    @Qualifier("cajeroRepository")
    private ICajeroRepository cajeroRepository;

    @Autowired
    @Qualifier("cocineroRepository")
    private ICocineroRepository cocineroRepository;

    @Override
    public List<Empleado> getAll() {
        return empleadoRepository.findAll();
    }

    @Override
    public List<Empleado> getAllActive() {
        return empleadoRepository.findByActivoTrue();
    }

    @Override
    public Empleado findById(Integer idEmpleado) {
        return empleadoRepository.findById(idEmpleado).orElse(null);
    }

    @Override
    public Empleado insertOrUpdate(Empleado empleado) {
        if (empleado instanceof Cajero) {
            return cajeroRepository.save((Cajero) empleado);
        } else if (empleado instanceof Cocinero) {
            return cocineroRepository.save((Cocinero) empleado);
        }
        return empleadoRepository.save(empleado);
    }
}
