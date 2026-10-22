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

	private IUserRepository userRepository;

	public UserService(IUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// --- MENSAJE DE CONTROL 1 ---
		System.out.println("\n[DEBUG 1] Spring Security está buscando en la BD al usuario: " + username);

		com.unla.TPGrupalOO2Hito2Grupo01.entities.User user =
				userRepository.findByUsernameAndFetchUserRolesEagerly(username);

		if (user == null) {
			// --- MENSAJE DE CONTROL 2 ---
			System.out.println("[DEBUG 2] ¡ATENCIÓN! La consulta JOIN FETCH devolvió NULL para: '" + username + "'");
			throw new UsernameNotFoundException("Usuario no encontrado o sin rol: " + username);
		}

		// --- MENSAJES DE CONTROL 3, 4 y 5 ---
		System.out.println("[DEBUG 3] ¡Usuario encontrado en BD!: " + user.getUsername());
		System.out.println("[DEBUG 4] Hash Bcrypt guardado en la BD: " + user.getPassword());
		System.out.println("[DEBUG 5] ¿El usuario está habilitado (enabled)?: " + user.isEnabled());

		if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
			System.out.println("[DEBUG 6] ¡ALERTA! El usuario existe pero la lista de roles está VACÍA.");
		} else {
			for (UserRole userRole : user.getUserRoles()) {
				System.out.println("[DEBUG 6] Rol mapeado en la colección: " + userRole.getRole().name());
			}
		}

		return buildUser(user, buildGrantedAuthorities(user.getUserRoles()));
	}


	private User buildUser(com.unla.TPGrupalOO2Hito2Grupo01.entities.User user, List<GrantedAuthority> grantedAuthorities) {
		return new User(user.getUsername(), user.getPassword(), user.isEnabled(),
						true, true, true, //accountNonExpired, credentialsNonExpired, accountNonLocked,
						grantedAuthorities);
	}

	private List<GrantedAuthority> buildGrantedAuthorities(Set<UserRole> userRoles) {
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		for (UserRole userRole : userRoles) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRole().name()));
		}
		return new ArrayList<>(grantedAuthorities);
	}
}
