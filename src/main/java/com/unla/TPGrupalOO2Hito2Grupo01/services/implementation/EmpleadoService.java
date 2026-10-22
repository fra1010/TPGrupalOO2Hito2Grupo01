package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cajero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cocinero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.User;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UserRole;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Role;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IEmpleadoRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.ICajeroRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.ICocineroRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUserRepository;
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

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Se hace opcional para que las pruebas (tests) no fallen si no hay servidor SMTP configurado
    @Autowired(required = false)
    private JavaMailSender javaMailSender;

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
        String passwordGenerada = null;
        boolean esNuevo = (empleado.getIdEmpleado() == null);

        // Si ya tiene ID es una modificacion ordinaria (edición)
        if (!esNuevo) {
            Empleado empleadoExistente = findById(empleado.getIdEmpleado());
            if (empleadoExistente != null && empleadoExistente.getUser() != null) {
                empleado.setUser(empleadoExistente.getUser());
            }
            return guardarPorTipo(empleado);
        }

        // Si es un alta nueva se verifica si el DNI ya existia
        Optional<Empleado> empleadoExistente = empleadoRepository.findByDni(empleado.getDni());

        if (empleadoExistente.isPresent()) {
            Empleado existente = empleadoExistente.get();

            if (!existente.isActivo()) {
                return reactivarEmpleadoEliminado(existente, empleado);
            } else {
                throw new IllegalArgumentException("Ya existe un empleado activo con el DNI: " + empleado.getDni());
            }
        }

        // --- NUEVA LÓGICA DE ALTA: Crear User, contraseña aleatoria y rol ---
        passwordGenerada = generarContraseniaAleatoria(8);

        User user = new User();
        user.setUsername(String.valueOf(empleado.getDni()));
        user.setPassword(passwordEncoder.encode(passwordGenerada));
        user.setEnabled(true);

        // Asignar rol EMPLEADO
        UserRole role = new UserRole(user, Role.EMPLEADO);
        user.getUserRoles().add(role);

        // Guardar el usuario primero para que tenga ID asignado
        userRepository.save(user);

        empleado.setUser(user);
        empleado.setActivo(true);

        Empleado empleadoGuardado = guardarPorTipo(empleado);

        // --- ENVIAR CORREO CON CREDENCIALES ---
        if (passwordGenerada != null) {
            enviarMailCredenciales(String.valueOf(empleado.getDni()), passwordGenerada);
        }

        return empleadoGuardado;
    }

    @Override
    @Transactional
    public Empleado reactivarEmpleadoEliminado(Empleado existente, Empleado datosNuevos) {
        existente.setNombre(datosNuevos.getNombre());
        existente.setApellido(datosNuevos.getApellido());
        existente.setFechaNacimiento(datosNuevos.getFechaNacimiento());
        existente.setIngreso(datosNuevos.getIngreso());
        existente.setActivo(true);

        if (existente.getUser() != null) {
            existente.getUser().setEnabled(true);
            userRepository.save(existente.getUser());
        }

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
            emp.setActivo(false);

            if (emp.getUser() != null) {
                emp.getUser().setEnabled(false);
                userRepository.save(emp.getUser());
            }

            guardarPorTipo(emp);
            return true;
        }
        return false;
    }

    private Empleado guardarPorTipo(Empleado empleado) {
        if (empleado instanceof Cajero) {
            return cajeroRepository.save((Cajero) empleado);
        } else if (empleado instanceof Cocinero) {
            return cocineroRepository.save((Cocinero) empleado);
        }
        return empleadoRepository.save(empleado);
    }

    private String generarContraseniaAleatoria(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }

    private void enviarMailCredenciales(String destinatarioDni, String password) {
        if (javaMailSender == null) {
            System.err.println("Aviso: JavaMailSender no está inicializado (entorno de pruebas). Se omite el envío de correo.");
            return;
        }
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destinatarioDni + "@epicentro.com");
            mensaje.setSubject("Credenciales de Acceso - Epicentro Gourmet");
            mensaje.setText("¡Bienvenido a Epicentro Gourmet!\n\n" +
                    "Tus datos de acceso para iniciar sesión en el sistema son:\n" +
                    "Usuario (DNI): " + destinatarioDni + "\n" +
                    "Contraseña temporal: " + password + "\n\n" +
                    "Por favor, iniciá sesión en la aplicación y actualizá tus datos.");
            javaMailSender.send(mensaje);
        } catch (Exception e) {
            System.err.println("Error al enviar el correo (verificar configuración SMTP): " + e.getMessage());
        }
    }
}