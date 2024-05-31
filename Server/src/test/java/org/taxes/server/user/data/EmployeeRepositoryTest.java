package org.taxes.server.user.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class EmployeeRepositoryTest {
	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void contextLoads() {
		assertNotNull(employeeRepository);
	}

	@Test
	public void testFindCompanyEmployee() {
		User user = new User();
		User company = new User();

		user = entityManager.persist(user);
		company = entityManager.persist(company);

		Employee employee = new Employee(new EmployeeId(user, company), null);
		employee = entityManager.persist(employee);
		entityManager.flush();

		assertEquals(Optional.of(employee), employeeRepository
				.findCompanyEmployee(user.getId(), company.getId()));
		assertEquals(Optional.empty(), employeeRepository
				.findCompanyEmployee(-1, -1));
	}
}