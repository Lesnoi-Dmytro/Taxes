package org.taxes.server.tax.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxTypeRepository
	extends JpaRepository<TaxType, Byte> {
	@Query("SELECT t FROM TaxType t WHERE t.salaryTax = true")
	List<TaxType> findAllSalaryTaxes();
}
