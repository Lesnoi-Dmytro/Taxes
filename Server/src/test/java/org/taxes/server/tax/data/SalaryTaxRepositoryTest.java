package org.taxes.server.tax.data;

import org.taxes.server.user.data.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class SalaryTaxRepositoryTest {
	@Autowired
	private SalaryTaxRepository salaryTaxRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void contentLoads() {
		assertNotNull(salaryTaxRepository);
	}

	@Test
	public void testFindByCompanyId() {
		User company1 = new User();
		User company2 = new User();
		company1 = entityManager.persist(company1);
		company2 = entityManager.persist(company2);

		TaxType type = new TaxType();
		type = entityManager.persist(type);

		Tax tax1 = new Tax();
		Tax tax2 = new Tax();
		Tax tax3 = new Tax();
		tax1.setUser(company1);
		tax1.setType(type);
		tax2.setUser(company1);
		tax2.setType(type);
		tax3.setUser(company1);
		tax3.setType(type);

		tax1 = entityManager.persist(tax1);
		tax2 = entityManager.persist(tax2);
		tax3 = entityManager.persist(tax3);

		List<SalaryTax> salaryTaxes = new java.util.ArrayList<>(List.of(
				new SalaryTax(),
				new SalaryTax(),
				new SalaryTax()
		));
		salaryTaxes.get(0).setTax(tax1);
		salaryTaxes.get(0).setCompany(company1);
		salaryTaxes.get(1).setTax(tax2);
		salaryTaxes.get(1).setCompany(company1);
		salaryTaxes.get(2).setTax(tax3);
		salaryTaxes.get(2).setCompany(company2);

		salaryTaxes.replaceAll(entity -> entityManager.persist(entity));
		entityManager.flush();

		assertEquals(salaryTaxes.subList(0, 2),
				salaryTaxRepository.findByCompanyId(company1.getId()));
		assertEquals(List.of(),
				salaryTaxRepository.findByCompanyId(-1));
	}
}