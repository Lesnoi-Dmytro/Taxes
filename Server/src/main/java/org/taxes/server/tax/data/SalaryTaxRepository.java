package org.taxes.server.tax.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryTaxRepository
	extends JpaRepository<SalaryTax, Integer> {
	List<SalaryTax> findByCompanyId(Integer id);
}
