package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cajero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Cocinero;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Empleado;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Role;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UnidadVenta;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.User;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.UserRole;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.ICajeroRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.ICocineroRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IEmpleadoRepository;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUnidadVentaRepository;
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

    @Autowired
    private IUnidadVentaRepository unidadVentaRepository;

    // Permite iniciar el contexto sin un servicio de correo.
    // Sin embargo, el alta se rechaza si no está disponible.
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

        if (empleado.getIdEmpleado() == null) {
            throw new IllegalArgumentException(
                    "Para dar de alta un empleado, utilice "
                            + "darDeAltaConUsuario() e indique el email."
            );
        }

        Empleado empleadoExistente = empleadoRepository
                .findById(empleado.getIdEmpleado())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el empleado que desea modificar."
                ));

        // Una edición no debe cambiar la cuenta ni activar al empleado.
        empleado.setUser(empleadoExistente.getUser());
        empleado.setActivo(empleadoExistente.isActivo());

        // Evita asignar el DNI de otro empleado durante la edición.
        Optional<Empleado> empleadoConDni =
                empleadoRepository.findByDni(empleado.getDni());

        if (empleadoConDni.isPresent()
                && !empleadoConDni.get().getIdEmpleado()
                .equals(empleado.getIdEmpleado())) {

            throw new IllegalArgumentException(
                    "Ya existe otro empleado con el DNI: "
                            + empleado.getDni()
            );
        }

        return guardarPorTipo(empleado);
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
                obtenerUnidadDelResponsable(usernameResponsable);

        // El alta nunca reactiva empleados existentes.
        // Se rechaza el DNI tanto si está activo como si está inactivo.
        if (empleadoRepository.findByDni(empleado.getDni()).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe un empleado con el DNI: "
                            + empleado.getDni()
                            + ". Si está inactivo, debe activarlo "
                            + "desde la lista de empleados."
            );
        }

        // También se rechazan las cuentas deshabilitadas.
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

        // Primero se envían las credenciales.
        // Si falla el envío, no se ejecutan los guardados.
        enviarMailCredenciales(emailNormalizado, passwordGenerada);

        // Solo después del envío se guarda el usuario.
        user = userRepository.save(user);

        empleado.setUser(user);
        empleado.setActivo(true);

        Empleado empleadoGuardado = guardarPorTipo(empleado);

        asignarAUnidad(unidad, empleadoGuardado);

        return empleadoGuardado;
    }

    @Override
    @Transactional
    public Empleado activarEmpleado(
            Integer idEmpleado,
            String usernameResponsable) {

        if (idEmpleado == null) {
            throw new IllegalArgumentException(
                    "Debe indicar el empleado que desea activar."
            );
        }

        UnidadVenta unidad =
                obtenerUnidadDelResponsable(usernameResponsable);

        Empleado empleado = empleadoRepository
                .findById(idEmpleado)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el empleado que desea activar."
                ));

        // Se compara por ID para no depender del equals de la entidad.
        boolean perteneceALaUnidad = unidad.getEmpleados().stream()
                .anyMatch(e -> idEmpleado.equals(e.getIdEmpleado()));

        if (!perteneceALaUnidad) {
            throw new IllegalArgumentException(
                    "No puede activar un empleado que no pertenece "
                            + "a su unidad de venta."
            );
        }

        if (empleado.isActivo()) {
            throw new IllegalArgumentException(
                    "El empleado ya se encuentra activo."
            );
        }

        if (empleado.getUser() == null) {
            throw new IllegalArgumentException(
                    "El empleado no tiene una cuenta de usuario asociada. "
                            + "No se puede activar desde esta operación."
            );
        }

        // Se conservan los datos, el usuario y la contraseña existentes.
        User user = empleado.getUser();
        user.setEnabled(true);

        empleado.setActivo(true);

        userRepository.save(user);

        return guardarPorTipo(empleado);
    }

    @Override
    @Transactional
    public boolean deleteLogico(int idEmpleado) {

        Optional<Empleado> objetoOptional =
                empleadoRepository.findById(idEmpleado);

        if (objetoOptional.isPresent()) {
            Empleado empleado = objetoOptional.get();
            empleado.setActivo(false);

            if (empleado.getUser() != null) {
                empleado.getUser().setEnabled(false);
                userRepository.save(empleado.getUser());
            }

            guardarPorTipo(empleado);
            return true;
        }

        return false;
    }

    private UnidadVenta obtenerUnidadDelResponsable(
            String usernameResponsable) {

        if (usernameResponsable == null
                || usernameResponsable.isBlank()) {

            throw new IllegalArgumentException(
                    "Debe indicar el responsable que realiza la operación."
            );
        }

        UnidadVenta unidad =
                unidadVentaRepository.traerUnidadVentaResponsable(
                        usernameResponsable
                );

        if (unidad == null) {
            throw new IllegalArgumentException(
                    "Solo un responsable con una unidad de venta asignada "
                            + "puede realizar esta operación."
            );
        }

        return unidad;
    }

    private void asignarAUnidad(
            UnidadVenta unidad,
            Empleado empleado) {

        if (!unidad.getEmpleados().contains(empleado)) {
            unidad.getEmpleados().add(empleado);
        }
    }

    private Empleado guardarPorTipo(Empleado empleado) {

        if (empleado instanceof Cajero) {
            return cajeroRepository.save((Cajero) empleado);
        }

        if (empleado instanceof Cocinero) {
            return cocineroRepository.save((Cocinero) empleado);
        }

        return empleadoRepository.save(empleado);
    }

    private String generarContraseniaAleatoria(int longitud) {

        String caracteres =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        + "abcdefghijklmnopqrstuvwxyz"
                        + "0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(longitud);

        for (int i = 0; i < longitud; i++) {
            sb.append(
                    caracteres.charAt(
                            random.nextInt(caracteres.length())
                    )
            );
        }

        return sb.toString();
    }

    private String normalizarEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe ingresar el email del empleado."
            );
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    @Transactional
    public Empleado cambiarEstadoEmpleado(
            Integer idEmpleado,
            boolean activo,
            String usernameResponsable) {

        if (idEmpleado == null) {
            throw new IllegalArgumentException(
                    "Debe indicar el empleado que desea modificar."
            );
        }

        UnidadVenta unidad =
                obtenerUnidadDelResponsable(usernameResponsable);

        Empleado empleado = empleadoRepository
                .findById(idEmpleado)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el empleado."
                ));

        boolean perteneceALaUnidad = unidad.getEmpleados().stream()
                .anyMatch(e -> idEmpleado.equals(e.getIdEmpleado()));

        if (!perteneceALaUnidad) {
            throw new IllegalArgumentException(
                    "No puede modificar un empleado que no pertenece "
                            + "a su unidad de venta."
            );
        }

        // Se conserva la restricción para registros sin cuenta.
        if (activo && empleado.getUser() == null) {
            throw new IllegalArgumentException(
                    "El empleado no tiene una cuenta de usuario asociada. "
                            + "No se puede activar desde esta operación."
            );
        }

        empleado.setActivo(activo);

        if (empleado.getUser() != null) {
            empleado.getUser().setEnabled(activo);
            userRepository.save(empleado.getUser());
        }

        return guardarPorTipo(empleado);
    }

    @Override
    public Empleado traerPorUsername(String username) {
        return empleadoRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No se encontró un empleado asociado al usuario: " + username));
    }

    private void enviarMailCredenciales(
            String emailDestinatario,
            String password) {

        if (javaMailSender == null) {
            throw new IllegalArgumentException(
                    "El servicio de correo no está configurado. "
                            + "No se realizó el alta del empleado."
            );
        }

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(emailDestinatario);
        mensaje.setSubject(
                "Credenciales de Acceso - Epicentro Gourmet"
        );
        mensaje.setText(
                "¡Bienvenido a Epicentro Gourmet!\n\n"
                        + "Tus datos de acceso para iniciar sesión son:\n"
                        + "Usuario: " + emailDestinatario + "\n"
                        + "Contraseña temporal: " + password + "\n\n"
                        + "Por favor, iniciá sesión y cambiá tu contraseña."
        );

        try {
            javaMailSender.send(mensaje);
        } catch (MailException e) {
            throw new IllegalArgumentException(
                    "No se pudo enviar el correo con las credenciales. "
                            + "No se realizó el alta del empleado.",
                    e
            );
        }
    }
}