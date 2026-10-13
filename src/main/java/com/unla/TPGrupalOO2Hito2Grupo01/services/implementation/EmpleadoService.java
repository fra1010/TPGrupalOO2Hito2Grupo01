package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import java.util.List;
import java.util.Optional;
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
import jakarta.transaction.Transactional;

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
    @Transactional
    public Empleado insertOrUpdate(Empleado empleado) {
        // Si ya tiene ID es una modificacion ordinaria
        if (empleado.getIdEmpleado() != null) {
            return guardarPorTipo(empleado);
        }

        // Si es un alta nueva se verifica si el DNI ya existia
        Optional<Empleado> empleadoExistente = empleadoRepository.findByDni(empleado.getDni());

        if (empleadoExistente.isPresent()) {
            Empleado existente = empleadoExistente.get();

            // Si el DNI existia pero estaba inactivo se reactiva segun hito 2
            if (!existente.isActivo()) {
                return reactivarEmpleadoEliminado(existente, empleado);
            } else {
                throw new IllegalArgumentException("Ya existe un empleado activo con el DNI: " + empleado.getDni());
            }
        }

        // Si es nuevo se activa por defecto
        empleado.setActivo(true);
        return guardarPorTipo(empleado);
    }

    @Override
    @Transactional
    public Empleado reactivarEmpleadoEliminado(Empleado existente, Empleado datosNuevos) {
        // Se actualizan los datos basicos del registro existente
        existente.setNombre(datosNuevos.getNombre());
        existente.setApellido(datosNuevos.getApellido());
        existente.setFechaNacimiento(datosNuevos.getFechaNacimiento());
        existente.setIngreso(datosNuevos.getIngreso());
        existente.setActivo(true); // Se vuelve a activar

        // Se actualizan los datos especificos de las subclases
        if (existente instanceof Cajero && datosNuevos instanceof Cajero) {
            ((Cajero) existente).setTurno(((Cajero) datosNuevos).getTurno());
        } else if (existente instanceof Cocinero && datosNuevos instanceof Cocinero) {
            ((Cocinero) existente).setCategoria(((Cocinero) datosNuevos).getCategoria());
            ((Cocinero) existente).setTareaEspecial(((Cocinero) datosNuevos).getTareaEspecial());
        }

        return guardarPorTipo(existente);
    }

    @Override
    @Transactional
    public boolean deleteLogico(int idEmpleado) {
        Optional<Empleado> objetoOptional = empleadoRepository.findById(idEmpleado);
        if (objetoOptional.isPresent()) {
            Empleado emp = objetoOptional.get();
            emp.setActivo(false); // Baja logica
            guardarPorTipo(emp);
            return true;
        }
        return false;
    }

    // Metodo auxiliar para guardar usando el repositorio que corresponda
    private Empleado guardarPorTipo(Empleado empleado) {
        if (empleado instanceof Cajero) {
            return cajeroRepository.save((Cajero) empleado);
        } else if (empleado instanceof Cocinero) {
            return cocineroRepository.save((Cocinero) empleado);
        }
        return empleadoRepository.save(empleado);
    }
}
