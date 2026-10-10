
package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IEmpleadoRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IEmpleadoService;

@Service("empleadoService")
public class EmpleadoService implements IEmpleadoService {

    private final IEmpleadoRepository empleadoRepository;

    public EmpleadoService(IEmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public List<Empleado> getAll() {
        return empleadoRepository.findAll();
    }

    @Override
    public List<Empleado> getAllActive() {
        return empleadoRepository.findByActivoTrue();
    }

    @Override
    public Optional<Empleado> findById(Integer idEmpleado) {
        return empleadoRepository.findById(idEmpleado);
    }

    @Override
    public Empleado insertOrUpdate(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }
}