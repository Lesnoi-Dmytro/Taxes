package org.taxes.server.user.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class UserTypeRepositoryTest {
	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserTypeRepository userTypeRepository;

	@Test
	public void testFindByName() {
		UserType userType = new UserType(null, "1", null);
		entityManager.persist(userType);
		entityManager.flush();

		assertEquals(userType, userTypeRepository.findByName("1"));
		assertEquals(null, userTypeRepository.findByName("2"));
	}
}