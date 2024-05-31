package org.taxes.server.tax.data;

import org.taxes.server.user.data.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class TaxRepositoryTest {
	@Autowired
	private TaxRepository taxRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void contentLoads() {
		assertNotNull(taxRepository);
	}

	@Test
	public void testFindByUserId() {
		TaxType type = new TaxType();
		type = entityManager.persist(type);

		User user1 = new User();
		User user2 = new User();
		user1 = entityManager.persist(user1);
		user2 = entityManager.persist(user2);

		List<Tax> taxes = new ArrayList<>(List.of(
				new Tax(),
				new Tax(),
				new Tax()
		));
		taxes.get(0).setType(type);
		taxes.get(0).setUser(user1);
		taxes.get(1).setType(type);
		taxes.get(1).setUser(user1);
		taxes.get(2).setType(type);
		taxes.get(2).setUser(user2);

		taxes.replaceAll(t -> entityManager.persist(t));
		entityManager.flush();

		assertEquals(taxes.subList(0, 2),
				taxRepository.findByUserId(user1.getId()));
	}
}