package org.taxes.server.user.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class UserRepositoryTest {
	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@Test
	public void tastFindByUsername() {
		UserType userType = new UserType(null, "1", null);
		userType = entityManager.persist(userType);

		User user = new User(null, "1", "1", LocalDate.now(), "1", "1",
				userType, null, null, null, null);
		user = entityManager.persist(user);
		entityManager.flush();

		assertEquals(Optional.of(user), userRepository.findByEmail(user.getEmail()));
		assertEquals(Optional.empty(), userRepository.findByEmail("2"));
	}

	@Test
	public void testFindByNames() {
		UserType userType = new UserType(null, "1", null);
		userType = entityManager.persist(userType);

		User user1 = new User(null, "John", "Doe", LocalDate.now(), "1", "1",
				userType, null, null, null, null);
		User user2 = new User(null, "John", "Dou", LocalDate.now(), "1", "1",
				userType, null, null, null, null);
		User user3 = new User(null, "Annie", "Bow", LocalDate.now(), "1", "1",
				userType, null, null, null, null);
		user1 = entityManager.persist(user1);
		user2 = entityManager.persist(user2);
		user3 = entityManager.persist(user3);
		entityManager.flush();

		assertEquals(List.of(user1, user2), userRepository.findByName("J"));
		assertEquals(List.of(user1), userRepository.findByName("John Doe"));
		assertEquals(List.of(), userRepository.findByName("Johnie"));
	}
}