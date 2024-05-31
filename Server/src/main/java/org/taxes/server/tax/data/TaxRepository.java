package org.taxes.server.tax.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxRepository
		extends JpaRepository<Tax, Integer> {
	List<Tax> findByUserId(Integer userId);
}
