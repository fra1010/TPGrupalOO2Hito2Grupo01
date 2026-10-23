package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cajero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cocinero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Role;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.User;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UserRole;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.ICajeroRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.ICocineroRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IEmpleadoRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUserRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IEmpleadoService;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUnidadVentaRepository;
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

    @Autowired
    private IUnidadVentaRepository unidadVentaRepository;

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
        // Para una edición ordinaria, conservamos el User ya asociado
        if (empleado.getIdEmpleado() != null) {
            Empleado empleadoExistente = findById(empleado.getIdEmpleado());

            if (empleadoExistente != null && empleadoExistente.getUser() != null) {
                empleado.setUser(empleadoExistente.getUser());
            }

            return guardarPorTipo(empleado);
        }

        // Un alta nueva debe usar obligatoriamente darDeAltaConUsuario()
        throw new IllegalArgumentException(
                "Para dar de alta un empleado, utilice darDeAltaConUsuario() e indique el email."
        );
    }

    @Override
    @Transactional
    public Empleado darDeAltaConUsuario(
            Empleado empleado,
            String email,
            String usernameResponsable) {

        if (empleado.getIdEmpleado() != null) {
            throw new IllegalArgumentException(
                    "El alta debe realizarse con un empleado sin ID."
            );
        }

        String emailNormalizado = normalizarEmail(email);

        UnidadVenta unidad =
                unidadVentaRepository.traerUnidadVentaResponsable(usernameResponsable);

        if (unidad == null) {
            throw new IllegalArgumentException(
                    "Solo un responsable con una unidad de venta asignada "
                            + "puede dar de alta empleados."
            );
        }

        Optional<Empleado> empleadoExistente =
                empleadoRepository.findByDni(empleado.getDni());

        if (empleadoExistente.isPresent()) {
            Empleado existente = empleadoExistente.get();

            if (existente.isActivo()) {
                throw new IllegalArgumentException(
                        "Ya existe un empleado activo con el DNI: "
                                + empleado.getDni()
                );
            }

            Empleado reactivado = reactivarEmpleadoEliminado(
                    existente,
                    empleado,
                    emailNormalizado
            );

            asignarAUnidad(unidad, reactivado);
            return reactivado;
        }

        if (userRepository.existsByUsername(emailNormalizado)) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario registrado con el email: "
                            + emailNormalizado
            );
        }

        String passwordGenerada = generarContraseniaAleatoria(12);

        User user = new User();
        user.setUsername(emailNormalizado);
        user.setPassword(passwordEncoder.encode(passwordGenerada));
        user.setEnabled(true);

        UserRole role = new UserRole(user, Role.EMPLEADO);
        user.getUserRoles().add(role);

        user = userRepository.save(user);

        empleado.setUser(user);
        empleado.setActivo(true);

        Empleado empleadoGuardado = guardarPorTipo(empleado);

        asignarAUnidad(unidad, empleadoGuardado);

        programarEnvioCredenciales(emailNormalizado, passwordGenerada);

        return empleadoGuardado;
    }

    private void asignarAUnidad(UnidadVenta unidad, Empleado empleado) {
        if (!unidad.getEmpleados().contains(empleado)) {
            unidad.getEmpleados().add(empleado);
        }
    }

    @Override
    @Transactional
    public Empleado reactivarEmpleadoEliminado(
            Empleado existente,
            Empleado datosNuevos,
            String email) {

        if (existente == null || existente.getIdEmpleado() == null) {
            throw new IllegalArgumentException(
                    "Debe indicar un empleado existente para reactivar."
            );
        }

        Empleado empleadoGuardado = empleadoRepository
                .findById(existente.getIdEmpleado())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el empleado que desea reactivar."
                ));

        if (empleadoGuardado.isActivo()) {
            throw new IllegalArgumentException(
                    "El empleado ya se encuentra activo."
            );
        }

        if (empleadoGuardado.getDni() != datosNuevos.getDni()) {
            throw new IllegalArgumentException(
                    "El DNI no coincide con el empleado que desea reactivar."
            );
        }

        boolean ambosCajeros =
                empleadoGuardado instanceof Cajero
                        && datosNuevos instanceof Cajero;

        boolean ambosCocineros =
                empleadoGuardado instanceof Cocinero
                        && datosNuevos instanceof Cocinero;

        if (!ambosCajeros && !ambosCocineros) {
            throw new IllegalArgumentException(
                    "Debe conservar el tipo original del empleado "
                            + "al reactivarlo: Cajero o Cocinero."
            );
        }

        String emailNormalizado = normalizarEmail(email);
        String passwordGenerada = null;

        if (empleadoGuardado.getUser() != null) {

            User user = empleadoGuardado.getUser();

            if (!emailNormalizado.equalsIgnoreCase(user.getUsername())) {
                throw new IllegalArgumentException(
                        "El empleado ya tiene un usuario asociado. "
                                + "Para reactivarlo, ingrese su email original. "
                                + "Esta operación no modifica el usuario."
                );
            }

            user.setEnabled(true);
            userRepository.save(user);

        } else {

            if (userRepository.existsByUsername(emailNormalizado)) {
                throw new IllegalArgumentException(
                        "Ya existe un usuario registrado con el email: "
                                + emailNormalizado
                );
            }

            passwordGenerada = generarContraseniaAleatoria(12);

            User user = new User();
            user.setUsername(emailNormalizado);
            user.setPassword(passwordEncoder.encode(passwordGenerada));
            user.setEnabled(true);

            UserRole role = new UserRole(user, Role.EMPLEADO);
            user.getUserRoles().add(role);

            user = userRepository.save(user);
            empleadoGuardado.setUser(user);
        }

        empleadoGuardado.setNombre(datosNuevos.getNombre());
        empleadoGuardado.setApellido(datosNuevos.getApellido());
        empleadoGuardado.setFechaNacimiento(datosNuevos.getFechaNacimiento());
        empleadoGuardado.setIngreso(datosNuevos.getIngreso());
        empleadoGuardado.setActivo(true);

        if (ambosCajeros) {
            ((Cajero) empleadoGuardado).setTurno(
                    ((Cajero) datosNuevos).getTurno()
            );
        } else {
            ((Cocinero) empleadoGuardado).setCategoria(
                    ((Cocinero) datosNuevos).getCategoria()
            );

            ((Cocinero) empleadoGuardado).setTareaEspecial(
                    ((Cocinero) datosNuevos).getTareaEspecial()
            );
        }

        Empleado resultado = guardarPorTipo(empleadoGuardado);

        if (passwordGenerada != null) {
            programarEnvioCredenciales(emailNormalizado, passwordGenerada);
        }

        return resultado;
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

    private String normalizarEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe ingresar el email del empleado."
            );
        }

        return email.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private void programarEnvioCredenciales( // para que no mande el correo si falla el guardado en la base
            String emailDestinatario,
            String password) {

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                    "El envío de credenciales requiere una transacción activa."
            );
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        enviarMailCredenciales(emailDestinatario, password);
                    }
                }
        );
    }


    private void enviarMailCredenciales(String emailDestinatario, String password) {

        if (javaMailSender == null) {
            System.err.println(
                    "Aviso: JavaMailSender no está inicializado. Se omite el envío de correo."
            );
            return;
        }

        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(emailDestinatario);
            mensaje.setSubject("Credenciales de Acceso - Epicentro Gourmet");
            mensaje.setText(
                    "¡Bienvenido a Epicentro Gourmet!\n\n" +
                            "Tus datos de acceso para iniciar sesión son:\n" +
                            "Usuario: " + emailDestinatario + "\n" +
                            "Contraseña temporal: " + password + "\n\n" +
                            "Por favor, iniciá sesión y cambiá tu contraseña."
            );

            javaMailSender.send(mensaje);

        } catch (MailException e) {
            System.err.println(
                    "Error al enviar el correo: " + e.getMessage()
            );
        }
    }
}