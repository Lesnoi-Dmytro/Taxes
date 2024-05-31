package org.taxes.server.tax.data;

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
class TaxTypeRepositoryTest {
	@Autowired
	private TaxTypeRepository taxTypeRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void contentLoads() {
		assertNotNull(taxTypeRepository);
	}

	@Test
	public void testFindAllSalaryTaxes() {
		List<TaxType> taxTypes = new ArrayList<>(List.of(
				new TaxType(),
				new TaxType(),
				new TaxType()
		));
		taxTypes.forEach(t -> t.setSalaryTax(true));
		taxTypes.get(2).setSalaryTax(false);
		taxTypes.replaceAll(t -> entityManager.persist(t));
		entityManager.flush();

		assertEquals(taxTypes.subList(0, 2),
				taxTypeRepository.findAllSalaryTaxes());
	}
}