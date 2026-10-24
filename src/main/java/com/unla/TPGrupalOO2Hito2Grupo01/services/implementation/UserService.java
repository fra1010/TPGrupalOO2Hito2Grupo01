package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unla.TPGrupalOO2Hito2Grupo01.dtos.UserDTO;
import com.unla.TPGrupalOO2Hito2Grupo01.entities.Role;
import com.unla.TPGrupalOO2Hito2Grupo01.services.IUser;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.UserRole;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUserRepository;

@Service("userService")
public class UserService implements UserDetailsService, IUser {

	private final IUserRepository userRepository;
    private final ModelMapper modelMapper;
	public UserService(IUserRepository userRepository,ModelMapper modelMapper) {
		this.userRepository = userRepository;
        this.modelMapper=modelMapper;
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {

		com.unla.TPGrupalOO2Hito2Grupo01.entities.User user =
				userRepository.findByUsernameAndFetchUserRolesEagerly(
						username
				);

		if (user == null) {
			throw new UsernameNotFoundException(
					"Usuario no encontrado o sin rol: " + username
			);
		}

		if (user.getUserRoles() == null
				|| user.getUserRoles().isEmpty()) {

			throw new UsernameNotFoundException(
					"El usuario no tiene roles asignados: " + username
			);
		}

		return buildUser(
				user,
				buildGrantedAuthorities(user.getUserRoles())
		);
	}

	private User buildUser(
			com.unla.TPGrupalOO2Hito2Grupo01.entities.User user,
			List<GrantedAuthority> grantedAuthorities) {

		return new User(
				user.getUsername(),
				user.getPassword(),
				user.isEnabled(),
				true, // accountNonExpired
				true, // credentialsNonExpired
				true, // accountNonLocked
				grantedAuthorities
		);
	}

	private List<GrantedAuthority> buildGrantedAuthorities(
			Set<UserRole> userRoles) {

		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

		for (UserRole userRole : userRoles) {
			grantedAuthorities.add(
					new SimpleGrantedAuthority(
							"ROLE_" + userRole.getRole().name()
					)
			);
		}

		return new ArrayList<>(grantedAuthorities);
	}

    @Override
    public UserDTO agregarUser(UserDTO user, Role role) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario registrado con el username: " + user.getUsername()
            );
        }
        BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
        com.unla.TPGrupalOO2Hito2Grupo01.entities.User userGuardar = new com.unla.TPGrupalOO2Hito2Grupo01.entities.User(
                user.getUsername(),
                pe.encode(user.getPassword()),
                true
        );
        userGuardar.getUserRoles().add(new UserRole(userGuardar, role));
        com.unla.TPGrupalOO2Hito2Grupo01.entities.User saved = userRepository.save(userGuardar);
        return modelMapper.map(saved, UserDTO.class);
    }
}