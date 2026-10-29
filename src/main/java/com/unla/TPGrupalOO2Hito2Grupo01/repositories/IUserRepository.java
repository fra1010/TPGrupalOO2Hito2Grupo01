package com.unla.TPGrupalOO2Hito2Grupo01.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unla.TPGrupalOO2Hito2Grupo01.entities.User;

import java.util.Optional;

@Repository("userRepository")
public interface IUserRepository extends JpaRepository<User, Integer> {

	// CAMBIO AQUI: Cambiamos JOIN FETCH por LEFT JOIN FETCH
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles WHERE u.username = :username")
	User findByUsernameAndFetchUserRolesEagerly(@Param("username") String username);

	boolean existsByUsername(String username);

	Optional<User> findByUsername(String username);
    @Query("""
    SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
    FROM User u JOIN Empleado e ON e.user = u
    WHERE u.username = :username AND TYPE(e) = Cajero
    """)
    boolean esCajero(@Param("username") String username);
}
