package com.unla.TPGrupalOO2Hito2Grupo01.services.implementation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.UserRole;
import com.unla.TPGrupalOO2Hito2Grupo01.repositories.IUserRepository;

@Service("userService")
public class UserService implements UserDetailsService {

	private final IUserRepository userRepository;

	public UserService(IUserRepository userRepository) {
		this.userRepository = userRepository;
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
}