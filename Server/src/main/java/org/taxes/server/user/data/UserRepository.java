package org.taxes.server.user.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository
	extends JpaRepository<User, Integer> {
	Optional<User> findByEmail(String email);

	@Query("SELECT u FROM User u WHERE CONCAT(u.firstName, ' ', u.lastName) " +
			"LIKE CONCAT('%', ?1, '%')")
	List<User> findByName(String name);
}
