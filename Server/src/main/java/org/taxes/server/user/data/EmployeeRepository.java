package org.taxes.server.user.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository
		extends JpaRepository<Employee, EmployeeId> {
	@Query("SELECT e FROM Employee e WHERE e.id.employee.id = ?1 AND e.id.company.id = ?2")
	Optional<Employee> findCompanyEmployee(Integer employeeId, Integer companyId);
}
