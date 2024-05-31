package org.taxes.server.user;

import lombok.RequiredArgsConstructor;
import org.taxes.server.user.data.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final EmployeeRepository employeeRepository;
	private final UserTypeRepository userTypeRepository;

	public List<User> getUserCompanies(Integer id) {
		User user = userRepository.findById(id).orElseThrow(() ->
				new IllegalArgumentException("User not found"));

		return user.getCompanies().stream()
				.map(c -> c.getId().getCompany())
				.toList();
	}

	public List<Employee> getCompanyEmployees(Integer id) {
		User company = userRepository.findById(id).orElseThrow(() ->
				new IllegalArgumentException("Company not found"));

		return company.getEmployees();
	}

	public List<User> getUsersWithName(String name, Integer companyId) {
		Stream<User> stream = userRepository.findByName(name).stream();
		UserType userType = userTypeRepository.findByName("USER");

		stream = stream.filter(u -> u.getType().getId().equals(userType.getId()));
		stream = stream.filter(u -> {
			for (Employee e : u.getCompanies()) {
				if (e.getId().getCompany().getId().equals(companyId)) {
					return false;
				}
			}
			return true;
		});
		return stream.toList();
	}

	public Employee createEmployee(AddEmployeeRequest employeeRequest, Integer companyId) {
		User company = userRepository.findById(companyId).orElseThrow(() ->
				new IllegalArgumentException("Company not found"));
		User employee = userRepository.findById(employeeRequest.getEmployeeId())
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));

		Employee newEmployee = new Employee(new EmployeeId(employee, company),
				employeeRequest.getHireDate());
		return employeeRepository.save(newEmployee);
	}

	public void deleteEmployee(Integer employeeId, Integer companyId) {
		Employee employee = employeeRepository.findCompanyEmployee(employeeId, companyId)
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));
		employeeRepository.delete(employee);
	}
}
